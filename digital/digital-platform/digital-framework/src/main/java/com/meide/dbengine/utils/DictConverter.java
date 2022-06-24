package com.meide.dbengine.utils;

import com.meide.common.utils.DictUtil;
import com.meide.common.utils.SysDictDetail;
import com.meide.dbengine.api.BaseApi;
import com.meide.dbengine.tableinfo.ColInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用于字典转换。
 */
public class DictConverter {

    private DictGetter dictGetter = null;
    public List<DictRecord> list = new ArrayList<>();


    public void setDictGetter(DictGetter dictGetter) {
        this.dictGetter = dictGetter;
    }

    /**
     * 添加配置  new DictConverter.Build("").set(..)
     * @param builder
     * @return
     */
    public DictConverter add(DictConverter.Builder builder) {
        list.addAll(builder.list);
        return this;
    }

    /**
     * 配置转换后缀。 默认 +Label
     * @param labelSuffix
     * @return
     */
    public DictConverter configSuffix(String labelSuffix){
        DictUtil.labelSuffix=labelSuffix;
        return this;
    }

    /**
     * 实际调用转换方法。
     *
     * @param baseApi
     * @param result
     * @return
     */
    public Object convert(BaseApi baseApi, Object result) {
        if (result == null || dictGetter == null) {
            return result;
        }
        //基本类型 不管
        if (result.getClass().isPrimitive() || result instanceof String) {
            return result;
        }
        //查找该接口中，表对应的字典值，如果没有，则不需要转
        String tableName = baseApi.getTableInfo().getName();
        List<DictRecord> dictRecords = list.stream().filter(dictRecord -> dictRecord.tableName.equals(tableName)).collect(Collectors.toList());
        if (null == dictRecords || dictRecords.isEmpty()) {
            return result;
        }
        //只转换map list
        List<SysDictDetail> sysDicts = dictGetter.getAllDictList();
        dictRecords.forEach(dictRecord -> {
            //这里fieldName应该转vo。
            if (dictRecord.fieldVoName == null) {
                List<ColInfo> cols = baseApi.getTableInfo().getCols();
                for (ColInfo c : cols) {
                    if (c.getName().equals(dictRecord.fieldName)) {
                        dictRecord.fieldVoName = c.getVoName();
                        break;
                    }
                }
            }
            if (dictRecord.fieldVoName != null)
                DictUtil.parseLabel(sysDicts, result, dictRecord.fieldVoName, dictRecord.groupCode, null);
        });
        return result;
    }


    public List<DictRecord> getList() {
        return list;
    }

    public void setList(List<DictRecord> list) {
        this.list = list;
    }

    //获取全部字典
    public interface DictGetter {
        List<SysDictDetail> getAllDictList();
    }

    public static class Builder {
        private String tableName;
        private List<DictRecord> list;

        public Builder(String tableName) {
            this.tableName = tableName;
            list = new ArrayList<>();
        }

        public Builder set(String fieldName, String groupCode) {
            list.add(new DictRecord(this.tableName, fieldName, groupCode));
            return this;
        }
    }

    public static class DictRecord {
        String tableName;
        String fieldName;
        String groupCode;
        //vo名称。
        String fieldVoName;

        public DictRecord(String tableName, String fieldName, String groupCode) {
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.groupCode = groupCode;
        }

    }
}
