package com.meide.dbengine.tableinfo;

import com.meide.dbengine.api.BaseApi;
import com.meide.dbengine.factory.EngineFactory;
import com.meide.dbengine.utils.EngineUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 独立抽象表列的全部信息
 */
public class TableInfos {
    private String dbName;
    private String dbType;
    private String dbUrl;
    private LinkedHashMap<String, TableInfo> tables;

    //生成的全部api接口
    private List<BaseApi> apiList = new ArrayList<>();

    public TableInfos(EngineFactory engineFactory) {
        this.dbName = engineFactory.getDbName();
        this.dbType = engineFactory.getDbType();
        this.dbUrl = engineFactory.getUrl();
    }


    //重新构建apiList
    public void rebuildApiList() {
        apiList.clear();
        for (TableInfo t : tables.values()) {
            apiList.addAll(t.getApiList());
        }
    }

    /**
     * 从sql查询的list<map>中转换实体类。
     *
     * @param tableList [{table_name,table_comment}]
     * @param colList   [{table_name column_name data_type column_comment pk is_nullable}]
     */
    public void setTableInfoListMap(List<Map<String, Object>> tableList, List<Map<String, Object>> colList,boolean camelVo) {
        tables = new LinkedHashMap<String, TableInfo>();

        for (Map<String, Object> map : tableList) {
            String tableName = EngineUtil.strVal(map.get("table_name"));
            String tableComment = EngineUtil.strVal(map.get("table_comment"));
            tables.put(tableName, new TableInfo(tableName, tableComment, new ArrayList<>()));
        }


        for (Map<String, Object> map : colList) {
            ColInfo colInfo = ColInfo.createFromMap(map,camelVo);
            if (null != colInfo) {
                TableInfo tableInfo = tables.get(colInfo.getTable());
                if (null == tableInfo) {
                    continue;
                }
                //主键放置第一位。以后默认第一位就是主键，无论是否实际是主键。
                if (colInfo.isPk() && tableInfo.getCols().size() > 0) {
                    tableInfo.getCols().add(0, colInfo);
                } else {
                    tableInfo.getCols().add(colInfo);
                }
            }
        }
        //如果没有主键  默认第一列为主键
        for (TableInfo t : tables.values()) {
            if (t.getCols().stream().noneMatch(c -> c.isPk())) {
                t.getCols().get(0).setPk(true);
            }
        }
    }


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public LinkedHashMap<String, TableInfo> getTables() {
        return tables;
    }

    public void setTables(LinkedHashMap<String, TableInfo> tables) {
        this.tables = tables;
    }

    public List<BaseApi> getApiList() {
        return apiList;
    }

    public void setApiList(List<BaseApi> apiList) {
        this.apiList = apiList;
    }
}
