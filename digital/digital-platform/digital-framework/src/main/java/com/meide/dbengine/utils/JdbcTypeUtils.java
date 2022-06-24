package com.meide.dbengine.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用来处理jdbc的映射
 */
public class JdbcTypeUtils {

    public static Map<String, String> map = new HashMap<String, String>();

    static {
        map.put("ARRAY", "ARRAY");
        map.put("BIT", "BIT");
        map.put("TINYINT", "TINYINT");
        map.put("SMALLINT", "SMALLINT");
        map.put("INTEGER", "INTEGER");
        map.put("INT", "INTEGER");


        map.put("BIGINT", "BIGINT");
        map.put("FLOAT", "FLOAT");
        map.put("REAL", "REAL");
        map.put("DOUBLE", "DOUBLE");
        map.put("NUMERIC", "NUMERIC");
        map.put("DECIMAL", "DECIMAL");
        map.put("CHAR", "CHAR");
        map.put("VARCHAR", "VARCHAR");
        map.put("LONGVARCHAR", "LONGVARCHAR");
        map.put("TEXT", "LONGVARCHAR");
        map.put("LONGTEXT", "LONGVARCHAR");


        map.put("DATE", "DATE");
        map.put("TIME", "TIME");
        map.put("DATETIME", "TIMESTAMP");
        map.put("TIMESTAMP", "TIMESTAMP");
        map.put("BINARY", "BINARY");
        map.put("VARBINARY", "VARBINARY");
        map.put("LONGVARBINARY", "LONGVARBINARY");
        map.put("NULL", "NULL");
        map.put("OTHER", "OTHER");
        map.put("BLOB", "BLOB");
        map.put("CLOB", "CLOB");
        map.put("BOOLEAN", "BOOLEAN");
        map.put("BOOL", "BOOLEAN");

        map.put("CURSOR", "CURSOR");
        map.put("NVARCHAR", "NVARCHAR");
        map.put("NCHAR", "NCHAR");
        map.put("NCLOB", "NCLOB");
        map.put("LONGNVARCHAR", "LONGNVARCHAR");
        map.put("STRUCT", "STRUCT");
        map.put("JAVA_OBJECT", "JAVA_OBJECT");
        map.put("DISTINCT", "DISTINCT");
        map.put("REF", "REF");
        map.put("DATALINK", "DATALINK");
        map.put("ROWID", "ROWID");
        map.put("SQLXML", "SQLXML");
        map.put("DATETIMEOFFSET", "DATETIMEOFFSET");
        map.put("TIME_WITH_TIMEZONE", "TIME_WITH_TIMEZONE");
        map.put("TIMESTAMP_WITH_TIMEZONE", "TIMESTAMP_WITH_TIMEZONE");
    }

    public static String getTypeDesc(String dbTypeDesc) {
        if (null == dbTypeDesc) {
            return "";
        }
        return map.get(dbTypeDesc.toUpperCase());
    }

    public static String getTypeMapperDesc(String dbTypeDesc) {
        if (null == dbTypeDesc) {
            return "";
        }
        return ",jdbcType=" + map.get(dbTypeDesc.toUpperCase());
    }

    public static void main(String[] args) {
        System.out.println(getTypeMapperDesc("varchar"));
    }
}
