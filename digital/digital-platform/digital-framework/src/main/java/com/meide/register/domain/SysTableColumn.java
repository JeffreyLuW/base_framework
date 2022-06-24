package com.meide.register.domain;

import com.meide.common.core.domain.OldBaseEntity;
import com.meide.register.converter.TableConverter;

/**
 * 数据库表名  information_schema.tables
 *
 * @author jiay
 */
public class SysTableColumn extends OldBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 数据库表名列名转化器 */
    TableConverter tableConverter;

    /**
     * 列名
     */
    private String columnName;
    /**
     * 字段数据库类型
     */
    private String dataType;

    /**
     * 字段java类型
     */
    private String javaType;


    /**
     * 是否主键
     */
    private String isPk;

    /**
     * 是否允许为空
     */
    private String isNullable;


    /**
     * 字段注释
     */
    private String columnComment;
    /**
     * 字段顺序
     */
    private Integer ordinalPosition;


    /**
     * 获取数据库真实字段
     *
     * @return
     */
    public String getRealColumnName() {
        return  columnName ;
    }


    /**
     * 获取前段页面显示字段
     *
     * @return
     */
    public String getVoColumnName( ) {
        return  tableConverter.getPageColumn(columnName);
    }


    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public Integer getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getIsPk() {
        return isPk;
    }

    public void setIsPk(String isPk) {
        this.isPk = isPk;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    public String getJavaType() {
        if ("varchar".endsWith(dataType)) {
            return "string";
        }
        if ("datetime".endsWith(dataType)) {
            return "date-time";
        }
        if ("bigint".endsWith(dataType)) {
            return "integer";
        }
        if ("char".endsWith(dataType)) {
            return "string";
        }
        if ("int".endsWith(dataType)) {
            return "integer";
        }
        if ("blob".endsWith(dataType)) {
            return "string";
        }
        return dataType;
    }



    public TableConverter getTableConverter() {
        return tableConverter;
    }

    public void setTableConverter(TableConverter tableConverter) {
        this.tableConverter = tableConverter;
    }

}
