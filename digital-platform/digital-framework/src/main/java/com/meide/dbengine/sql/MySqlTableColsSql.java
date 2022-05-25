package com.meide.dbengine.sql;

import com.meide.dbengine.factory.EngineFactory;

/**
 * mysql提取表列信息。
 */
public class MySqlTableColsSql implements TableColsSql {
    @Override
    public boolean support(EngineFactory engineFactory, String dbType) {
        return "mysql".equals(dbType);
    }

    @Override
    public String getTableSql(EngineFactory engineFactory, String dbName) {
        return " SELECT table_name,table_comment FROM information_schema.`TABLES` WHERE table_schema = '" + dbName + "' ";
    }

    @Override
    public String getColsSql(EngineFactory engineFactory, String dbName) {
        String sql = "SELECT \n" +
                "  table_name,\n" +
                "  column_name,\n" +
                "  data_type,\n" +
                "  column_comment,\n" +
                "  character_maximum_length len,\n"+
                "  ordinal_position,\n" +
                "  (\n" +
                "    CASE\n" +
                "      WHEN column_key = 'PRI' \n" +
                "      THEN '1' \n" +
                "      ELSE '0' \n" +
                "    END\n" +
                "  ) AS pk,\n" +
                "  is_nullable \n" +
                "FROM\n" +
                "  information_schema.columns \n" +
                "WHERE table_schema = '" + dbName + "' \n" +
                "ORDER BY table_name,\n" +
                "  ordinal_position ";
        return sql;
    }


}
