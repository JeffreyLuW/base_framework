package com.meide.dbengine.tableinfo;

import com.meide.dbengine.utils.EngineUtil;
import com.meide.dbengine.utils.Underline2Camel;

import java.util.Map;

public class ColInfo {
    //table_name column_name data_type column_comment pk is_nullable
    private String table;
    private String name;
    private String type;
    private String comment;
    private Integer len;
    private boolean pk;
    private boolean nullable;
    private String voName;

    public ColInfo() {
    }

    public static ColInfo createFromMap(Map<String, Object> map, boolean camelVo) {
        if (null == map || map.isEmpty()) {
            return null;
        }
        ColInfo info = new ColInfo();
        info.table = map.get("table_name").toString();
        info.name = map.get("column_name").toString();

        if (camelVo) {
            info.voName = Underline2Camel.underline2Camel(info.name, true);
        } else {
            info.voName = info.name;
        }

        info.type = map.get("data_type").toString();
        info.comment = map.get("column_comment").toString();
        info.pk = EngineUtil.boolValue(map.get("pk"));
        info.nullable = EngineUtil.boolValue(map.get("is_nullable"));

        info.len = EngineUtil.parseInt(map.get("len"), -1);

        return info;
    }


    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        String lenStr = this.type + (this.len == null || this.len < 1 ? "" : "(" + len + ")");
        if (this.comment == null) {
            return lenStr;
        }
        return this.comment + "|" + lenStr;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getVoName() {
        return voName;
    }

    public void setVoName(String voName) {
        this.voName = voName;
    }

    public String getSwaggerType() {
//        type  //string,datetime:string|date-time,object,boolean, float,double,integer,long
        if (null == type) {
            return "string";
        }
        String rs = "string";
        String lowerType = type.toLowerCase();
        if (lowerType.contains("bigint")) {
            rs = "long";
        } else if (lowerType.contains("int")) {
            rs = "integer";
        } else if (lowerType.contains("time")) {
            rs = "datetime";
        } else if (lowerType.contains("decimal")) {
            rs = "float";
        }
        return rs;
    }
}
