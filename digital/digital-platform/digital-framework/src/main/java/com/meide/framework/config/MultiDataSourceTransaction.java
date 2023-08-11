package com.meide.framework.config;

import com.meide.common.enums.DataSourceType;
import com.meide.framework.datasource.DynamicDataSourceContextHolder;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jiay
 * @date 2023/8/10
 */
public class MultiDataSourceTransaction implements Transaction{
    private final DataSource dataSource;

    private ConcurrentMap<String, Connection> concurrentMap;

    private boolean autoCommit;


    public MultiDataSourceTransaction(DataSource dataSource) {
        this.dataSource = dataSource;
        concurrentMap = new ConcurrentHashMap<>();
    }


    @Override
    public Connection getConnection() throws SQLException {
        String databaseIdentification = DynamicDataSourceContextHolder.getDataSourceType();
        if (StringUtils.isEmpty(databaseIdentification)) {
            databaseIdentification = DataSourceType.MASTER.name();
        }
        //获取数据源
        if (!this.concurrentMap.containsKey(databaseIdentification)) {
            try {
                Connection conn = this.dataSource.getConnection();
                autoCommit=false;
                conn.setAutoCommit(false);
                this.concurrentMap.put(databaseIdentification, conn);
            } catch (SQLException ex) {
                throw new CannotGetJdbcConnectionException("Could bot get JDBC otherConnection", ex);
            }
        }
        return this.concurrentMap.get(databaseIdentification);
    }


    @Override
    public void commit() throws SQLException {
        for (Connection connection : concurrentMap.values()) {
            if (!autoCommit) {
                connection.commit();
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        for (Connection connection : concurrentMap.values()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        for (Connection connection : concurrentMap.values()) {
            DataSourceUtils.releaseConnection(connection, this.dataSource);
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
