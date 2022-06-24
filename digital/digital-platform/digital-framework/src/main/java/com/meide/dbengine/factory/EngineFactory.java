package com.meide.dbengine.factory;

import com.meide.dbengine.DbChainCreater;
import com.meide.dbengine.DbChainRunner;
import com.meide.dbengine.api.*;
import com.meide.dbengine.processors.AllowByTablePrefix;
import com.meide.dbengine.processors.BaseProcessorChain;
import com.meide.dbengine.processors.NoInsertPkChain;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.sql.MySqlTableColsSql;
import com.meide.dbengine.sql.TableColsSql;
import com.meide.dbengine.tableinfo.TableInfo;
import com.meide.dbengine.tableinfo.TableInfos;
import com.meide.dbengine.utils.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通过table定义，能获取对应table的信息、sql、以及调用。
 */
public class EngineFactory implements ApplicationContextAware {
    public ApplicationContext applicationContext;
    private SqlSessionFactory sqlSessionFactory;
    private Configuration configuration;
    private RequestMappingHandlerMapping requestMappingHandlerAdapter;
    private SqlSessionTemplate sqlSessionTemplate;
    private MapperHelper mapperHelper;
    private String url;
    private String dbType;
    private String dbName;

    private TableInfos tableInfos;
    private boolean camelVo = true;//是否字段转驼峰。
    private DbChainCreater dbChainCreater;

    //允许拦截处理数据库和接口创建。
    private List<BaseProcessorChain<TableInfos>> dbTableInfoChains = new ArrayList<>();

    //用于创建表对应的接口。
    private List<Class<? extends BaseApi>> apiClasses = Arrays.asList(
            SelectApi.class, DeleteApi.class, ListApi.class,
            InsertApi.class, InsertSelectiveApi.class,
            UpdateApi.class, UpdateSelectiveApi.class
    );

    /**
     * 统一配置BaseApi忽略的列。
     */
    private HashMap<Class<? extends BaseApi>, HashMap<String, Set<String>>> ignoredCols = new HashMap<>();

    /**
     * 用于获取表列信息。应该提供多数据库支持。
     */
    private List<TableColsSql> tableColsSqls = Arrays.asList(new MySqlTableColsSql());

    /**
     * 用于执行实际接口。
     */
    private DbChainRunner chainRunner;
    //允许拦截处理数据库和接口创建。
    private List<BaseProcessorChain<RunningInfo>> runningChains = new ArrayList<>();

    /**
     * 逻辑删除的配置。
     */
    private LogicDeleteConfig logicDeleteConfig = new LogicDeleteConfig();

    /**
     * 字典转换。
     */
    private DictConverter dictConverter = new DictConverter();

    boolean isDev;

    public EngineFactory() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        this.configuration = sqlSessionFactory.getConfiguration();
        this.sqlSessionTemplate = new SqlSessionTemplate(this.sqlSessionFactory);
        //用于动态执行mapper
        mapperHelper = new MapperHelper(configuration, sqlSessionTemplate);
        //获取Mapping适配器
        requestMappingHandlerAdapter = applicationContext.getBean(RequestMappingHandlerMapping.class);

        //dev
        String activeProfiles = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        isDev = "dev".equals(activeProfiles);

        initDbInfo();
        initChainRunner();
        //实际构建
        this.build();
    }

    private void initChainRunner() {
        chainRunner = new DbChainRunner();
        //添加用户自定义处理器。
        for (BaseProcessorChain<RunningInfo> runningInfoBaseProcessorChain : runningChains) {
            chainRunner.addChain(runningInfoBaseProcessorChain);
        }
    }

    //实际创建
    public void build() {
        this.processTableInfos();
        if (this.tableInfos == null) return;
        //构建mapper
        for (Map.Entry<String, TableInfo> entry : this.tableInfos.getTables().entrySet()) {
            TableInfo tableInfo = entry.getValue();
            if (tableInfo.getApiList() == null || tableInfo.getApiList().isEmpty()) {
                continue;
            }
            String nameSpace = "com.meide.autoapi." + entry.getKey();
            StringBuilder sb = new StringBuilder();
            for (BaseApi baseApi : tableInfo.getApiList()) {
                sb.append(baseApi.buildSql());
                sb.append("\n");
            }
            addToStatement(nameSpace, sb.toString());
        }
        //根据tableInfos注册接口。
        for (BaseApi api : this.tableInfos.getApiList()) {
            registerApi(api);
            //注册到swagger
            SwaggerUtil.addSwaggerRegister(api);
        }
    }

    //生成mapper解析到mybatis中去。
    private boolean addToStatement(String id, String innerSqlDef) {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"" + id + "\">\n" + innerSqlDef + "\n</mapper>";
        String resourceId = id;
        boolean rs = false;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes("UTF-8"));
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(bis,
                    configuration, resourceId, configuration.getSqlFragments());
            xmlMapperBuilder.parse();
            rs = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse mapping resource: '" + s + "'");
        }
        return rs;
    }


    //注册接口。
    private void registerApi(BaseApi baseApi) {
        ApiRealRunner apiRealRunner = new ApiRealRunner(this, baseApi);
        Method method = null;
        try {
            method = ApiRealRunner.class.getMethod("call", HashMap.class, HttpServletRequest.class, HttpServletResponse.class);
            Method invocableMethod = AopUtils.selectInvocableMethod(method, ApiRealRunner.class);
            RequestMappingInfo.Builder requestMappingInfoBuilder = new RequestMappingInfoBuilder(baseApi.getApiUrl());
            requestMappingInfoBuilder.methods(RequestMethod.POST);
            RequestMappingInfo requestMappingInfo = requestMappingInfoBuilder.build();
            requestMappingHandlerAdapter.registerMapping(requestMappingInfo, apiRealRunner, invocableMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加table处理器。
     *
     * @param baseProcessorChain
     * @return
     */
    public EngineFactory addTableInfoChain(BaseProcessorChain<TableInfos> baseProcessorChain) {
        dbTableInfoChains.add(baseProcessorChain);
        return this;
    }

    /**
     * 添加运行时的拦截处理
     *
     * @param baseProcessorChain
     * @return
     */
    public EngineFactory addRunningChain(BaseProcessorChain<RunningInfo> baseProcessorChain) {
        runningChains.add(baseProcessorChain);
        return this;
    }

    /**
     * 处理数据库表列信息。
     */
    public void processTableInfos() {
        //获取数据库表列信息。
        this.tableInfos = fetchDbTableInfo();
        if (null == this.tableInfos) {
            return;
        }
        //创建链式处理器。
        dbChainCreater = new DbChainCreater();
        //可以添加默认的处理器。
        //...
        try {
            //添加用户的处理器。处理数据库表信息。允许删除表列 调整vo。
            for (BaseProcessorChain<TableInfos> baseProcessorChain : dbTableInfoChains) {
                dbChainCreater.addChain(baseProcessorChain);
            }
            this.tableInfos = (TableInfos) dbChainCreater.run(this.tableInfos);
            this.tableInfos.rebuildApiList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //一些额外的初始化。 比如获取数据库的基本信息。
    private void initDbInfo() {
        SqlSession sqlSession = null;
        Connection conn = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            conn = sqlSession.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            url = metaData.getURL();
            dbType = metaData.getDatabaseProductName().toLowerCase();
            dbName = EngineUtil.getDbName(url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            EngineUtil.closeAutoCloseable(conn);
            EngineUtil.closeAutoCloseable(sqlSession);
        }
    }

    /**
     * 查询数据库的表定义。
     *
     * @return
     */
    public TableInfos fetchDbTableInfo() {
        TableColsSql targetSql = null;
        for (TableColsSql sql : tableColsSqls) {
            if (sql.support(this, this.dbType)) {
                targetSql = sql;
                break;
            }
        }
        if (null == targetSql) {
            return null;
        }
        String tablesStr = targetSql.getTableSql(this, this.dbName);
        String colsStr = targetSql.getColsSql(this, this.dbName);

        TableInfos tableInfos = new TableInfos(this);
        tableInfos.setTableInfoListMap(
                EngineUtil.exeQuerySql(sqlSessionFactory, tablesStr),
                EngineUtil.exeQuerySql(sqlSessionFactory, colsStr), camelVo
        );
        //标记设定表为逻辑删除
        logicDeleteConfig.doConfigTableInfos(tableInfos);
        //给table创建对应接口。
        for (Map.Entry<String, TableInfo> entry : tableInfos.getTables().entrySet()) {

            for (Class<? extends BaseApi> c : apiClasses) {
                try {
                    //api实例化。
                    BaseApi api = c.newInstance();
                    if (api.support(this.dbType)) {
                        api.setIgnoredTableCols(ignoredCols.get(c));
                        api.setEngineFactory(this);
                        api.setTableInfo(entry.getValue());
                        api.setMapperNamespace("com.meide.autoapi." + entry.getKey());
                        //表信息和全局的apiList都记录一遍。指向同一个API对象。
                        entry.getValue().getApiList().add(api);
                        tableInfos.getApiList().add(api);
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableInfos;
    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public RequestMappingHandlerMapping getRequestMappingHandlerAdapter() {
        return requestMappingHandlerAdapter;
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public String getUrl() {
        return url;
    }

    public String getDbType() {
        return dbType;
    }

    public String getDbName() {
        return dbName;
    }

    /**
     * 获取数据库所有表列信息。
     *
     * @return
     */
    public TableInfos getTableInfos() {
        return tableInfos;
    }

    public MapperHelper getMapperHelper() {
        return mapperHelper;
    }


    public DbChainRunner getChainRunner() {
        return chainRunner;
    }

    public boolean isDev() {
        return isDev;
    }

    /**
     * 设置只生成指定类型的接口 支持的接口类型如下：
     * ListApi.class, SelectApi.class, DeleteApi.class, InsertApi.class, InsertSelectiveApi.class,UpdateApi.class, UpdateSelectiveApi.class
     *
     * @param cs
     * @return
     */
    public EngineFactory setApiType(Class<? extends BaseApi>... cs) {
        apiClasses = Arrays.asList(cs);
        return this;
    }

    /**
     * 根据表格前缀生成指定接口。
     *
     * @param prefixs
     * @return
     */
    public EngineFactory generateByTablePrefix(String... prefixs) {
        if (null == prefixs || prefixs.length <= 0) {
            return this;
        }
        addTableInfoChain(new AllowByTablePrefix(prefixs));
        return this;
    }

    /**
     * 字段是否转驼峰。
     *
     * @param camelVo
     * @return
     */
    public EngineFactory setCamelVo(boolean camelVo) {
        this.camelVo = camelVo;
        return this;
    }

    /**
     * 配置忽略的列。
     *
     * @param c        BaseApi的子类Class
     * @param table    表名 * 表示匹配全部表。否则为指定表。
     * @param colNames 对应列。
     * @return
     */
    public EngineFactory addIgnoredCols(Class<? extends BaseApi> c, String table, String... colNames) {
        HashMap<String, Set<String>> tableIgnoredCols = ignoredCols.get(c);
        if (null == tableIgnoredCols) {
            tableIgnoredCols = new HashMap<>();
            ignoredCols.put(c, tableIgnoredCols);
        }
        if (tableIgnoredCols.get(table) == null) {
            tableIgnoredCols.put(table, new HashSet<>());
        }
        tableIgnoredCols.get(table).addAll(Arrays.asList(colNames).stream().collect(Collectors.toSet()));
        return this;
    }

    /**
     * 插入时，不插入主键列|第一列。
     *
     * @return
     */
    public EngineFactory setNotInsertPrimaryKey() {
        addTableInfoChain(new NoInsertPkChain());
        return this;
    }

    /**
     * 用于设置逻辑删除。
     * 注意逻辑删除字段，应该有默认值，且为默认不删除。
     * 因为逻辑删除字段，在插入、修改时会默认忽略，只有删除时会操作。
     *
     * @return
     */
    public LogicDeleteConfig getLogicDeleteConfig() {
        return logicDeleteConfig;
    }

    /**
     * 获取字典转换配置。
     * 通过 add方法添加表字典设置。
     *
     * @return
     */
    public DictConverter getDictConverter() {
        return dictConverter;
    }
}
