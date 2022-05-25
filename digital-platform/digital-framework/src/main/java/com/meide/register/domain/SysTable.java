package com.meide.register.domain;

import com.meide.common.core.domain.OldBaseEntity;
import com.meide.register.converter.TableConverter;

import java.util.List;

/**
 * 数据库表名  information_schema.tables
 *
 * @author jiay
 */
public class SysTable extends OldBaseEntity
{



    /** 数据库中表名 */
    private String tableName;

    /** 主键名称 */
    private SysTableColumn pkColumn ;


    /** 主键名称 */
    private SysTableColumn firstColum ;

    /** 列名 */
    private List<SysTableColumn> columnList;

    /** 数据库表名列名转化器 */
    TableConverter tableConverter;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<SysTableColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<SysTableColumn> columnList) {
        this.columnList = columnList;
    }

    public SysTableColumn getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(SysTableColumn pkColumn) {
        this.pkColumn = pkColumn;
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * 获取前段页面显示表名
     * @return
     */
    public String getVoTableName() {
        return tableConverter.getVoTableName (tableName) ;
    }

    /**
     * 获取数据库真实表名
     * @return
     */
    public String getRealTableName() {
        return    tableName  ;
    }

    public SysTableColumn getFirstColum() {
        return firstColum;
    }

    public void setFirstColum(SysTableColumn firstColum) {
        this.firstColum = firstColum;
    }

    public TableConverter getTableConverter() {
        return tableConverter;
    }

    public void setTableConverter(TableConverter tableConverter) {
        this.tableConverter = tableConverter;
    }
}
