package com.meide.common.core.dict;

/**
 * 提供字典方法给common中的类使用
 * @date 2021.12.03
 * @author jiay
 */
public interface ICommonDictService {
    /**
     * 查找字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    String getDictLabel(String dictType, String dictValue, String separator);

    /**
     * 查找字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    String getDictValue(String dictType, String dictLabel, String separator);
}
