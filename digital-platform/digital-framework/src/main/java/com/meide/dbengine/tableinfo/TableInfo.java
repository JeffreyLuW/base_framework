package com.meide.dbengine.tableinfo;

import com.meide.dbengine.api.BaseApi;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {
    private String name;
    private String comment;
    private List<ColInfo> cols;
    private List<BaseApi> apiList = new ArrayList<>();

    /**
     * 逻辑删除字段。为空表示物理删除。否则为逻辑删除。
     * 逻辑删除需要提供两个默认值，如，删除值-1,未删除值-0
     */
    private String logicDeleteField;
    private Object logicDeleteValue = 1;
    private Object logicNoDeleteValue = 0;


    public TableInfo(String name, String comment, List<ColInfo> cols) {
        this.name = name;
        this.comment = comment;
        this.cols = cols;
    }

    public List<BaseApi> getApiList() {
        return apiList;
    }

    public void setApiList(List<BaseApi> apiList) {
        this.apiList = apiList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ColInfo> getCols() {
        return cols;
    }

    public void setCols(List<ColInfo> cols) {
        this.cols = cols;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogicDeleteField() {
        return logicDeleteField;
    }

    /**
     * 如果逻辑删除字段不为空，且实际字段中包含该字段，则为逻辑删除表。
     *
     * @return
     */
    public boolean isLogicDelete() {
        return logicDeleteField != null;
    }

    public void setLogicDeleteField(String logicDeleteField) {
        this.logicDeleteField = logicDeleteField;
    }

    public Object getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(Object logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public Object getLogicNoDeleteValue() {
        return logicNoDeleteValue;
    }

    public void setLogicNoDeleteValue(Object logicNoDeleteValue) {
        this.logicNoDeleteValue = logicNoDeleteValue;
    }
}
