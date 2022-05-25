package com.meide.dbengine.api;

import com.meide.dbengine.factory.EngineFactory;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.tableinfo.ColInfo;
import com.meide.dbengine.tableinfo.TableInfo;
import com.meide.dbengine.utils.SwaggerRegister;
import io.swagger.models.Swagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 定义各种API的一个基类。
 */
public abstract class BaseApi implements Api, SwaggerRegister {
    protected EngineFactory engineFacotry;
    protected TableInfo tableInfo;
    protected List<ColInfo> cols;
    protected String namespace;
    //默认第一列为主键
    protected ColInfo pkCol;
    private HashMap<String, Set<String>> ignoredTableCols;

    @Override
    public void setIgnoredTableCols(HashMap<String, Set<String>> tableCols) {
        this.ignoredTableCols = tableCols;
    }

    @Override
    public void setEngineFactory(EngineFactory engineFacotry) {
        this.engineFacotry = engineFacotry;
    }

    @Override
    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        this.pkCol = tableInfo.getCols().get(0);

        this.copyCols();
    }

    //每个接口都有默认需要处理的列。
    protected void copyCols() {
        //当前的列是个浅复制。可以随意增删，不影响其他接口列。
        this.cols = new ArrayList<>();
        for (ColInfo c : tableInfo.getCols()) {
            if (!isIgnoredCols(c)) {
                this.cols.add(c);
            }
        }
    }

    /**
     * 判断当前列是否需要忽略
     *
     * @param c
     * @return
     */
    protected boolean isIgnoredCols(ColInfo c) {
        //如果该表是逻辑删除字段，则应该对比是否是逻辑删除字段。
        if (tableInfo.isLogicDelete() && c.getName().equals(tableInfo.getLogicDeleteField())) {
            return true;
        }
        //有忽略字段时
        if (ignoredTableCols != null && !ignoredTableCols.isEmpty()) {
            //指定表指定了忽略
            Set<String> tableIgnoredSet = ignoredTableCols.get(c.getTable());
            if (tableIgnoredSet != null && tableIgnoredSet.contains(c.getName())) {
                return true;
            }
            //全局表 指定了忽略
            Set<String> globalTableIgnoredSet = ignoredTableCols.get("*");
            if (globalTableIgnoredSet != null && globalTableIgnoredSet.contains(c.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TableInfo getTableInfo() {
        return this.tableInfo;
    }

    @Override
    public List<ColInfo> getCols() {
        return this.cols;
    }

    @Override
    public void removeCols(String... colNames) {
        for (int i = this.cols.size() - 1; i >= 0; i--) {
            boolean inCols = false;
            for (String delCol : colNames) {
                if (this.cols.get(i).getName().equals(delCol)) {
                    inCols = true;
                    break;
                }
            }
            if (inCols) {
                this.cols.remove(i);
            }
        }
    }


    @Override
    public void setMapperNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getMapperId() {
        return this.namespace + "." + getName();
    }

    @Override
    public String getApiUrl() {
        return "/auto-api/" + this.tableInfo.getName() + "/" + this.getName();
    }

    /**
     * 根据数据库可能有不同的实现。也可能是一致的。
     *
     * @param dbType
     * @return
     */
    public abstract boolean support(String dbType);

    /**
     * 不能和其他类型的API同名。决定了sql的id 和 url的路径。
     * 查看 {@link this#getMapperId()} {@link this#getApiUrl()}
     *
     * @return
     */
    public abstract String getName();

    /**
     * 这里的sql的di应该和 getName()保持一致。
     * 如果没有sql，当前接口，不应该被注册。
     *
     * @return
     */
    public abstract String buildSql();

    /**
     * 实际http调用的时候，调用该方法。自己处理参数和mapper的调用
     *
     * @param runningInfo
     * @return
     * @throws Exception
     */
    public abstract Object apiCall(RunningInfo runningInfo) throws Exception;

    /**
     * 注册到swagger接口中去。
     *
     * @param swagger
     */
    public abstract void registerToSwagger(Swagger swagger);
}
