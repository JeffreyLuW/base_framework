package com.meide.register.domain;


import io.swagger.annotations.ApiModelProperty;

/**
 * 查询请求参数类
 *
 * @author jiay
 */
public class DataApiQueryBean {

    /* 需要给接口隐藏，用不到 */
    @ApiModelProperty(value = "操作表名", hidden = true)
    private String tableName;

    @ApiModelProperty(value = "查询的列名", required = true)
    private String columnName;

    @ApiModelProperty(value = "具体数值", required = true)
    private String columnValue;

    @ApiModelProperty(value = "查询类型", required = true)
    private String selectType;

    @ApiModelProperty(value = "当前记录起始索引", required = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页显示记录数", required = false)
    private Integer pageSize;

    @ApiModelProperty(value = "排序列", required = false)
    private String orderByColumn;

    @ApiModelProperty(value = "排序的方向desc或者asc", required = false)
    private String isAsc = "asc";


    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }



    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }
}
