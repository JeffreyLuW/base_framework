package com.meide.dbengine.sql;

import com.meide.dbengine.factory.EngineFactory;

/**
 * 用于生成不同数据库的列查询sql。返回字段：
 *
 */
public interface TableColsSql {
    boolean support(EngineFactory engineFactory, String dbType);

    /**
     *  [{table_name,table_comment}]
     * @param engineFactory
     * @param dbName
     * @return
     */
    String getTableSql(EngineFactory engineFactory, String dbName);

    /**
     * table_name column_name data_type column_comment pk is_nullable
     * @param engineFactory
     * @param dbName
     * @return
     */
    String getColsSql(EngineFactory engineFactory, String dbName);
}
