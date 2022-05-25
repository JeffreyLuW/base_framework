package com.meide.common.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * 用于快速测试和Spring上下文无关 和配置无关 和Web无关的类。 如普通class mapper service
 * Mapper的java和xml默认同名。否则自行修改代码--搜索 allMapperNames
 * @author jiay
 */
public class MybatisHelper {

    private static String aliasPkg = "com.meide";//支持任意包名的一部分。 .domain  .vo  .model等等
    private static String javaSourceDir = "src.main.java";//默认java源码放在该位置

    private static List<File> pathList;
    public static HashMap<String, String> pathPkgMapping = null;//文件和包名的相互匹配
    private static SqlSessionFactory factory;
    private static SqlSession session;
    static DataSource dataSource;


    /**
     * 初始化基本配置。
     *
     * @param aliasPkg       模型类 VO的包名，支持任意包名的一部分。 .domain  .vo  .model等等
     * @param mapperClassSub mapper命名后缀 比如XXXMapper。java 则这里是 Mapper
     * @param findImpClass   可空。用于一些接口类寻找实现类
     */
    public static void initConfig(DataSource dataSource, String aliasPkg,
                                  String mapperClassSub, InstanceHelper.FindImpClass findImpClass
    ) {
        MybatisHelper.dataSource = dataSource;
        MybatisHelper.aliasPkg = aliasPkg;

        if (null != mapperClassSub)
            InstanceHelper.mapperClassSuffix = mapperClassSub;
        InstanceHelper.findImpClass = findImpClass == null ? InstanceHelper.defaultServiceImpl() : findImpClass;
    }

    //忽略的路径--根据情况配置和修改
    private static boolean isIgnoredPath(String absPath) {
        return absPath.contains("\\target")
                || absPath.contains("\\test")
                || absPath.contains("\\digital-ui")
                || absPath.contains("\\dist")
                || absPath.contains("\\bin")
                || absPath.contains("\\.idea")
                || absPath.contains("\\.git")
                || absPath.contains("\\idea")
                || absPath.contains("\\resources\\static");
    }


    public static String toJson(Object obj, boolean pretty) {
        if (null == obj) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (pretty)
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 由此获取对象实例 开始
     *
     * @param c
     * @param <T>
     * @return
     */
    public static <T extends Object> T getInstance(Class<T> c) {
        InstanceHelper.setMapperFactory(new InstanceHelper.MapperFactory() {
            @Override
            public <T> T create(HashSet<Class> allMapperClasses, Class<T> c) {
                initMyBatis(allMapperClasses);
                try {
                    return getSession().getMapper(c);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        return InstanceHelper.getInstance(c);
    }

    //执行操作后，记得提交和关闭session！
    public static void commitAndCloseSession() {
        if (session != null) {
            session.commit();
            session.close();
        }
    }

    //获取项目根目录
    public static String findParentFile() {
        return System.getProperty("user.dir");
    }

    //查询全部文件
    private static List<File> searchAllFile() {
        if (null != pathList) return pathList;
        scanInitFileAndPkg();
        return pathList;
    }

    //查询全部文件和class的匹配关系
    public static HashMap<String, String> searchAllPkgMapping() {
        if (null != pathPkgMapping) return pathPkgMapping;
        scanInitFileAndPkg();
        return pathPkgMapping;
    }

    private static void scanInitFileAndPkg() {
        pathList = new ArrayList<>();
        pathPkgMapping = new HashMap<>();
        String rootFilePath = findParentFile();
        searchAllFileToList(rootFilePath, pathList);

        // Domain.java
        for (File f : pathList) {
            String name = f.getName();
            String absPath = f.getAbsolutePath();
            String relPath = absPath.substring(rootFilePath.length());
            String mayPkgName = null;
            //注册别名-- domain 文件夹下
            if (name.endsWith(".java")) {
                mayPkgName = relPath.replaceAll("[\\\\/]", ".");
                if (mayPkgName.startsWith(".")) {
                    mayPkgName = mayPkgName.substring(1);
                }
                if (mayPkgName.indexOf(javaSourceDir) > -1) {
                    mayPkgName = mayPkgName.substring(mayPkgName.indexOf(javaSourceDir) + javaSourceDir.length() + 1);
                }
                pathPkgMapping.put(absPath, mayPkgName);
                pathPkgMapping.put(mayPkgName, absPath);
            }
        }
    }

    //代理mybatis的config
    private static Configuration enhancerConfiguration(Configuration configuration) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Configuration.class);
        enhancer.setCallback(new ConfigurationMethodInterceptor(configuration));
        return (Configuration) enhancer.create();
    }


    //代理Configuration方法实现
    private static class ConfigurationMethodInterceptor implements MethodInterceptor {
        Configuration configuration;
        ObjectFactory objectFactory = null;
        ObjectWrapperFactory objectWrapperFactory = null;
        ReflectorFactory reflectorFactory = null;

        public ConfigurationMethodInterceptor(Configuration configuration) {
            this.configuration = configuration;
            objectFactory = configuration.getObjectFactory();
            objectWrapperFactory = configuration.getObjectWrapperFactory();
            reflectorFactory = configuration.getReflectorFactory();
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            //拦截 newMetaObject()方法，替换返回的 MetaObject 中的 objectWrapper
            if (method.getName().equals("newMetaObject")) {
                Object object = objects != null && objects.length > 0 ? objects[0] : null;
                MetaObject metaObject = MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
                ObjectWrapper objectWrapper = null;
                Field field = MetaObject.class.getDeclaredField("objectWrapper");
                field.setAccessible(true);
                objectWrapper = (ObjectWrapper) field.get(metaObject);
                field.set(metaObject, this.enhancerObjectWrapper(metaObject, objectWrapper, object));
                return metaObject;
            }
            return method.invoke(configuration, objects);
        }

        //!!核心，处理了对应的列名匹配规则
        private String doWithColumnName(String name) {
            if (name.startsWith("f_") || name.startsWith("F_")) {
                name = name.substring(2);
            }
            return name;
        }

        private Object enhancerObjectWrapper(MetaObject metaObject, ObjectWrapper objectWrapper, Object object) {

            if (object instanceof ObjectWrapper) {
                objectWrapper = (ObjectWrapper) object;
            } else if (objectWrapperFactory.hasWrapperFor(object)) {
                objectWrapper = objectWrapperFactory.getWrapperFor(metaObject, object);
            } else if (object instanceof Map) {
                objectWrapper = new MapWrapper(metaObject, (Map) object) {
                    @Override
                    public String findProperty(String name, boolean useCamelCaseMapping) {
                        return super.findProperty(doWithColumnName(name), useCamelCaseMapping);
                    }
                };
            } else if (object instanceof Collection) {
                objectWrapper = new CollectionWrapper(metaObject, (Collection) object) {
                    @Override
                    public String findProperty(String name, boolean useCamelCaseMapping) {
                        return super.findProperty(doWithColumnName(name), useCamelCaseMapping);
                    }

                };
            } else {
                objectWrapper = new BeanWrapper(metaObject, object) {
                    @Override
                    public String findProperty(String name, boolean useCamelCaseMapping) {
                        return super.findProperty(doWithColumnName(name), useCamelCaseMapping);
                    }
                };
            }

            return objectWrapper;
        }
    }


    //初始化
    private static void initMyBatis(HashSet<Class> allMapperClasses) {
        if (factory != null) return;
        try {
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource);

            Configuration configuration = new Configuration(environment);
            configuration.setLazyLoadingEnabled(true);
            configuration.setLogImpl(StdOutImpl.class);
            configuration = enhancerConfiguration(configuration);

            //多数据库配置
//            DatabaseIdProvider databaseIdProvider = new Application().getDatabaseIdProvider();
//            environment = configuration.getEnvironment();
//            if (environment != null && databaseIdProvider != null) {
//                String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
//                configuration.setDatabaseId(databaseId);
//            }


            //这里默认了Mapper.java和Mapper.xml同名 mapper 转 Haset<String> Mapper.xml的名称
            HashSet<String> allMapperNames = new HashSet<>();
            if (null != allMapperClasses)
                for (Class c : allMapperClasses) {
                    allMapperNames.add(c.getSimpleName() + ".xml");
                }
            List<File> pathList = searchAllFile();
            HashMap<String, String> pathPkgMapping = searchAllPkgMapping();
            // Domain.java
            for (File f : pathList) {
                String name = f.getName();
                String absPath = f.getAbsolutePath();
                String mayPkgName = pathPkgMapping.get(absPath);
                //注册别名
                if (mayPkgName != null && null != aliasPkg && mayPkgName.contains(aliasPkg)) {
                    try {
                        String className = mayPkgName.replace(".java", "");
                        Class domainC = Class.forName(className);
                        configuration.getTypeAliasRegistry().registerAlias(domainC);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (allMapperNames.contains(name)) {
                    //添加个Mapper 和要实例化的Class同名  因此MapperClass不能重复
                    XMLMapperBuilder xMLMapperBuilder = new XMLMapperBuilder(
                            new FileInputStream(new File(f.getAbsolutePath())),
                            configuration, f.getAbsolutePath(), configuration.getSqlFragments());
                    xMLMapperBuilder.parse();
                    System.out.println("add mapper:" + name);
                }
            }


            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            factory = builder.build(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化MyBatis 获取session
    private static SqlSession getSession() throws Exception {
        if (session == null) {
            if (null == factory) initMyBatis(null);
            session = factory.openSession();
        }
//        session.commit();
        return session;
    }


    private static void searchAllFileToList(String path, List<File> pathList) {
        File f = new File(path);
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File subFile : files) {
                if (subFile.isDirectory()) {
                    String absPath = subFile.getAbsolutePath();
                    if (isIgnoredPath(absPath)) {
                    } else {
                        searchAllFileToList(subFile.getAbsolutePath(), pathList);
                    }
                } else {
                    pathList.add(subFile);
                }
            }
        } else {
            pathList.add(f);
        }
    }

    //执行sql操作 查询或者update。 程序结束后调用  commitAndCloseSession();
    public static Object exeSql(String sql, Object params[]) {
        Object finalRs = null;//最终结果 list 或者 int
        try {
            boolean isSelect = sql.trim().toLowerCase().startsWith("select");
            Connection connection = getSession().getConnection();
            PreparedStatement pst = connection.prepareStatement(sql);
            if (sql.contains("?") && params != null && params.length > 0) {
                for (int i = 1; i <= params.length; i++) {//Object p:params
                    Object p = params[i - 1];
                    if (p instanceof Byte) {
                        pst.setByte(i, (Byte) p);
                    } else if (p instanceof Short) {
                        pst.setShort(i, (Short) p);
                    } else if (p instanceof Integer) {
                        pst.setInt(i, (Integer) p);
                    } else if (p instanceof Long) {
                        pst.setLong(i, (Long) p);
                    } else if (p instanceof Float) {
                        pst.setFloat(i, (Float) p);
                    } else if (p instanceof Double) {
                        pst.setDouble(i, (Double) p);
                    } else if (p instanceof String) {
                        pst.setString(i, (String) p);
                    } else {
                        pst.setObject(i, p);
                    }
                }
            }
            if (isSelect) {
                ArrayList list = new ArrayList();
                ResultSet rs = pst.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                while (rs.next()) {
                    int c = meta.getColumnCount();
                    HashMap row = new HashMap();
                    for (int i = 1; i <= c; i++) {
                        String colName = meta.getColumnName(i);
                        Object val = rs.getObject(i);
                        row.put(colName, val);
                    }
                    list.add(row);
                }
                finalRs = list;
            } else {
                finalRs = pst.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return finalRs;
    }

    public static void main(String[] args) throws Exception {
        long startTime = new Date().getTime();
//测试Mapper
//        SzybjcxxMapper c = getInstance(SzybjcxxMapper.class);
//        System.out.println(toJson(c.selectSzybjcxx("be622448-3abc-4d5f-90e1-9d79dd346125"), true));

//测试Sql select update
//        String sql = "select * from t_bas_dmtzlxx where rownum<2";
//        System.out.println(toJson(
//                exeSql(sql, new String[]{"9ab9f525-9fc2-4a35-8102-ebc0814ef532"}),
//                true
//        ));

        commitAndCloseSession();
        System.out.println("use time:" + (new Date().getTime() - startTime));
    }

}
