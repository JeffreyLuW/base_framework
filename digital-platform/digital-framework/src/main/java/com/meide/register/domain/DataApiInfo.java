package com.meide.register.domain;

import java.util.HashMap;

public class DataApiInfo {


    private String tableName;
    private SysTable table;

    private String apiUrl;

    private String apiUrlReMark;

    private String apiType;


    private HashMap params;


    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }


    public String getApiUrlReMark() {
        return apiUrlReMark;
    }

    public void setApiUrlReMark(String apiUrlReMark) {
        this.apiUrlReMark = apiUrlReMark;
    }

    public HashMap getParams() {
        return params;
    }

    public void setParams(HashMap params) {
        this.params = params;
    }

    public SysTable getTable() {
        return table;
    }

    public void setTable(SysTable table) {
        this.table = table;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
