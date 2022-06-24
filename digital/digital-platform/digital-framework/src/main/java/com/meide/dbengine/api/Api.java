package com.meide.dbengine.api;

import com.meide.dbengine.factory.EngineFactory;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.tableinfo.ColInfo;
import com.meide.dbengine.tableinfo.TableInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface Api {
    void setEngineFactory(EngineFactory engineFacotry);

    boolean support(String dbType);

    public String getName();

    void setTableInfo(TableInfo tableInfo);

    TableInfo getTableInfo();

    List<ColInfo> getCols();

    void removeCols(String... colNames);

    /**
     * 构建sql语句
     *
     * @return
     */
    String buildSql();

    /**
     * 设置sql在mapper中的命名空间。 自己根据拼接的sql-id和命名空间，可以组成mapper_id来调用mapper。
     */
    void setMapperNamespace(String namespace);

    /**
     * 获取mapper调用id
     *
     * @return
     */
    String getMapperId();

    /**
     * 获取接口地址
     *
     * @return
     */
    String getApiUrl();

    /**
     * 调用接口 mapper的内部实现。
     *
     * @param runningInfo
     * @return
     * @throws Exception
     */
    Object apiCall(RunningInfo runningInfo) throws Exception;


    /**
     * 设置忽略的表列。
     * @param tableCols
     */
    void setIgnoredTableCols(HashMap<String, Set<String>> tableCols);
}
