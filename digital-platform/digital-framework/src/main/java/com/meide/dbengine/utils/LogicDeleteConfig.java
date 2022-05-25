package com.meide.dbengine.utils;

import com.meide.dbengine.tableinfo.TableInfo;
import com.meide.dbengine.tableinfo.TableInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * 快速构建逻辑删除的表。
 * 注意逻辑删除字段，应该有默认值，且为默认不删除。
 */
public class LogicDeleteConfig {
    private List<TableLogicDelete> list = new ArrayList<>();

    public LogicDeleteConfig set(String logicDeleteField, Object logicDeleteValue, Object logicNoDeleteValue, String tableName) {
        if (null != tableName)
            list.add(new TableLogicDelete(tableName, logicDeleteField, logicDeleteValue, logicNoDeleteValue));
        return this;
    }

    /**
     * 设置表格是否为逻辑删除。
     *
     * @param tableInfos
     */
    public void doConfigTableInfos(TableInfos tableInfos) {
        if (null == tableInfos || tableInfos.getTables() == null || list.isEmpty()) return;
        for (TableInfo tableInfo : tableInfos.getTables().values()) {
            for (TableLogicDelete del : list) {
                del.configTableInfo(tableInfo);
            }
        }
    }


    //记录逻辑删除的表信息。
    public static class TableLogicDelete {
        String tableName;
        String logicDeleteField = "is_del";
        Object logicDeleteValue = 1;
        Object logicNoDeleteValue = 0;

        public TableLogicDelete(String tableName, String logicDeleteField, Object logicDeleteValue, Object logicNoDeleteValue) {
            this.tableName = tableName;
            this.logicDeleteField = logicDeleteField;
            this.logicDeleteValue = logicDeleteValue;
            this.logicNoDeleteValue = logicNoDeleteValue;
        }

        /**
         * 配置表信息。
         *
         * @param tableInfo
         */
        public void configTableInfo(TableInfo tableInfo) {
            //表名相等，或者表为*
            if (tableName != null && (
                    tableName.equals("*") || tableName.equals(tableInfo.getName())
            )) {
                //是否字段匹配。
                boolean hasRightLogicDeleteField = tableInfo.getCols().stream().anyMatch(c -> c.getName().equals(logicDeleteField));
                if(hasRightLogicDeleteField){
                    tableInfo.setLogicDeleteField(logicDeleteField);
                    tableInfo.setLogicDeleteValue(logicDeleteValue);
                    tableInfo.setLogicNoDeleteValue(logicNoDeleteValue);
                }
            }
        }
    }
}
