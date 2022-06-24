package com.meide.common.utils;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.util.*;


/**
 * 用于字典表自动解析字典字典，自动赋值给  ${field}+${labelSuffix} 对应的属性
 * <p>
 * 简单使用:   {@link DictUtil#parseLabel(List, Object, String, String, OnGetLabel)}
 * 多字段使用: {@link DictUtil#builder(List, Object, OnGetLabel)#parse(String, String)}
 * @author jiay
 */
public class DictUtil {

    /**
     * 字段对应的后缀
     */
    public static  String labelSuffix = "Label";

    //用于实例对象的字典列表和需要处理的target
    private List<SysDictDetail> sysDicts;
    private Object target;
    private OnGetLabel onGetLabel;

    /**
     * 解析对应字典字典的label显示
     *
     * @param sysDicts
     * @param target   支持List Map 普通JavaBean
     * @param code
     * @param field
     */
    public static void parseLabel(List<SysDictDetail> sysDicts, Object target, String field, String code, OnGetLabel onGetLabel) {
        if (target instanceof List) {
            for (Object t : (List) target) {
                parseLabel(sysDicts, t, field, code, onGetLabel);
            }
            return;
        }
        String label = null;
        String value = null;
        try {
            value = getValue(target, field);
            if (null != value) {
                for (SysDictDetail dict : sysDicts) {
                    if (dict.getGroupCode().equals(code) && valueStrEq(dict.getValue(), value)) {
                        label = dict.getName();
                        break;
                    }
                }
            }
            if (null != label) {
                boolean goOn = true;
                if (null != onGetLabel) goOn = onGetLabel.OnGetLabel(target, field, code, value, label);
                if (goOn) setValue(target, field, label);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //只要字符串相等，则字典值匹配。
    private static boolean valueStrEq(Object a, Object b) {
        return String.valueOf(a).equals(String.valueOf(b));
    }

    /**
     * 配合 {@link this#parse(String, String)} 使用
     * 如果只解析一个字段，可以使用  {@link DictUtil#parseLabel(List, Object, String, String, OnGetLabel)}
     *
     * @param sysDicts 全部字典表数据
     * @param target   支持List Map 普通JavaBean
     * @return
     */
    public static DictUtil builder(List<SysDictDetail> sysDicts, Object target, OnGetLabel onGetLabel) {
        DictUtil dictUtil = new DictUtil();
        dictUtil.sysDicts = sysDicts;
        dictUtil.target = target;
        dictUtil.onGetLabel = onGetLabel;
        return dictUtil;
    }

    /**
     * 追加要解析的字段
     *
     * @param code
     * @param field
     * @return
     */
    public DictUtil parse(String field, String code) {
        parseLabel(this.sysDicts, this.target, field, code, this.onGetLabel);
        return this;
    }

    private static SysDictDetail mockDict(String code, String value, String name) {
        SysDictDetail sysDict = new SysDictDetail();
        sysDict.setGroupCode(code);
        sysDict.setValue(value);
        sysDict.setName(name);
        return sysDict;
    }

    //获取对象的值。
    private static String getValue(Object target, String field) throws Exception {
        Object tmp = null;
        if (target instanceof Map) {
            tmp = ((Map) target).get(field);
        } else {
            try {
                String methodName = "get" + field.toUpperCase().substring(0, 1) + field.substring(1);
                Method method = MethodUtils.getAccessibleMethod(target.getClass(), methodName, new Class[]{});
                if (null != method) {
                    method.setAccessible(true);
                    tmp = method.invoke(target);
                }
            } catch (Exception e) {

            }

        }
        return tmp == null ? null : String.valueOf(tmp);
    }

    //给某个值赋值。
    private static void setValue(Object target, String field, String value) throws Exception {
        String labelFieldName = field + labelSuffix;
        if (target instanceof Map) {
            ((Map) target).put(labelFieldName, value);
        } else {
            try {
                String methodName = "set" + labelFieldName.toUpperCase().substring(0, 1) + labelFieldName.substring(1);
                Method method = MethodUtils.getAccessibleMethod(target.getClass(), methodName, new Class[]{String.class});
                method.invoke(target, value);
            } catch (Exception e) {

            }
        }
    }

    /**
     * 用于监听dict的value-label的匹配。
     * 可以拦截默认的赋值行为，然后自行处理解析后的值。
     */
    public interface OnGetLabel {
        /**
         * @param item
         * @param field
         * @param code
         * @param value
         * @param label
         * @return 是否继续赋值
         */
        boolean OnGetLabel(Object item, String field, String code, String value, String label);
    }

    public static void main(String[] args) throws Exception {
        List<SysDictDetail> sysDicts = new ArrayList<>();
        sysDicts.add(mockDict("dict_sex", "1", "男"));
        sysDicts.add(mockDict("dict_sex", "2", "女"));
        sysDicts.add(mockDict("dict_yw", "1", "有"));
        sysDicts.add(mockDict("dict_yw", "0", "无"));


        HashMap<String, String> map = new HashMap<>();
        map.put("name", "sdf");
        map.put("type", "2");
        map.put("exists", "1");

        HashMap<String, String> map2 = new HashMap<>();
        map2.put("name", "sdf");
        map2.put("type", "1");
        map2.put("exists", "0");


        Object target2 = Arrays.asList(map, map2);

        //-----------------*** start parse **------
        builder(sysDicts, target2, null)
                .parse("type", "dict_sex")
                .parse("exists", "dict_yw");

        //or
        parseLabel(sysDicts, target2, "type", "dict_sex", null);

        //-----------------*** end parse **------
        System.out.println("target2==============\r\n");
        System.out.println(target2);

        System.out.println(StringUtils.toUnderScoreCase("_text_ok"));
        System.out.println("sdfsdf");
    }
}
