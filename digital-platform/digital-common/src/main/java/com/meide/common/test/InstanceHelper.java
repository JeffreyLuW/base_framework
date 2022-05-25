package com.meide.common.test;

import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 根据class实例化对应的
 * @author jiay
 */
public class InstanceHelper {

    //判断当前是否是mapper文件  类名含Mapper且是Interface
    public static String mapperClassSuffix = "Mapper";

    //记录本次实例化需要的mapper类  mybatis实例化
    private static HashSet<Class> mapperClasses = new HashSet<>();
    //记录本次实例化需要的service接口类(其实现类为普通类，存于normalClasses中)，不可实例化
    private static HashSet<Class> serviceIClasses = new HashSet<>();
    //记录本次实例化需要的service类 可以正常实例化的类。
    private static HashSet<Class> normalClasses = new HashSet<>();
    //记录本次实例化需要的service类
    private static HashSet<Class> allClasses = new HashSet<>();

    //本次实例化的service 接口和impl类对应map
    private static HashMap<Class, Class> serviceiAndImpl = new HashMap<>();

    //本次实例化的对象
    private static HashMap<Class, Object> initedObjects = new HashMap<>();

    //用于外部创建mapper
    private static MapperFactory mapperFactory;
    //要忽略不实例化的类型
    private static final Class[] ignoredClass = new Class[]{
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, BigDecimal.class, Boolean.class, Character.class,
            String.class, List.class, Map.class, HttpServlet.class, HttpRequest.class, MultipartFile.class, Logger.class
    };

    public static FindImpClass findImpClass = defaultServiceImpl();//查找实例化的类的方法。

    /**
     * 设置mapper生成器--mybatis处理
     *
     * @param mapperFactory
     */
    public static void setMapperFactory(MapperFactory mapperFactory) {
        InstanceHelper.mapperFactory = mapperFactory;
    }

    //实例化某个class
    public static <T extends Object> T getInstance(Class<T> c) {
        findAllClass(c);
        //prepare special class ,eg MapperClasss
        initMappers();
        //实例化普通类
        return (T) initNormalClass(c);
    }

    //判断某个类是否应该被忽略
    private static boolean isIgnoredClass(Class c) {
        if (c.isPrimitive()) {
            return true;
        }
        if (c.getName().contains("org.springframework")) {
            return true;
        }
        boolean rs = false;
        for (Class mc : ignoredClass) {
            if (c.isAssignableFrom(mc)) {
                rs = true;
                break;
            }
        }
        return rs;
    }

    //查找service的实现类
    public static FindImpClass defaultServiceImpl() {
        return new FindImpClass() {
            @Override
            public Class findImplClass(Class c) {
                Class rs = null;
                //添加对应的实现类
                String simpleName = c.getSimpleName();
                if (simpleName.toLowerCase().contains("service")) {
                    //如果是service 去掉I前缀 查找
                    if (simpleName.startsWith("I"))
                        simpleName = simpleName.substring(1);
                    String implNames[] = new String[]{
                            c.getPackage().getName() + ".impl." + simpleName + "Impl",
                            c.getPackage().getName() + ".impl." + simpleName + "Imp",
                            c.getPackage().getName() + ".impl." + simpleName,
                            c.getPackage().getName() + "." + simpleName + "Impl",
                            c.getPackage().getName() + "." + simpleName
                    };
                    HashMap<String, String> pathPkgMapping = MybatisHelper.searchAllPkgMapping();
                    for (String implName : implNames) {
                        String absFile = pathPkgMapping.get(implName + ".java");
                        try {
                            if (null != absFile)
                                rs = Class.forName(implName);
                        } catch (Exception e) {

                        }
                        if (null != rs) break;
                    }
                }
                if (rs == null)
                    System.out.println("no find interface impl!!!" + c);
                return rs;
            }
        };
    }

    private static <T extends Object> T initNormalClass(Class<T> c) {
        Object obj = null;
        try {
            if (initedObjects.containsKey(c)) {
                obj = initedObjects.get(c);
            } else {
                obj = c.newInstance();
                initedObjects.put(c, obj);
                //实例化属性
                Field[] fields = c.getDeclaredFields();
                for (Field f : fields) {
                    //如果是Mapper
                    if (mapperClasses.contains(f.getType())) {
                        f.setAccessible(true);
                        //赋值
                        f.set(obj, initedObjects.get(f.getType()));
                        continue;
                    }

                    //如果是service
                    if (serviceIClasses.contains(f.getType())) {
                        f.setAccessible(true);
                        //赋值
                        f.set(obj, initNormalClass(serviceiAndImpl.get(f.getType())));
                        continue;
                    }
                    //判断是否是普通的class
                    if (normalClasses.contains(f.getType())) {
                        f.setAccessible(true);
                        //赋值
                        f.set(obj, initNormalClass(f.getType()));
                        continue;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) obj;
    }

    //实例化mapper
    private static void initMappers() {
        if (mapperFactory == null) {
            System.out.println("please set mapperFactory first!!");
            return;
        }
        for (Class c : mapperClasses) {
            System.out.println("init Mappers:" + c);
            Object instance = mapperFactory.create(mapperClasses, c);
            if (instance != null)
                initedObjects.put(c, instance);
        }
    }

    //查询全部的class
    private static void findAllClass(Class c) {
        searchAllClassWithCb(c, new OnGetClass() {

            @Override
            public void onGetMapperClass(Class f) {
                mapperClasses.add(f);
            }

            @Override
            public void onGetServiceClass(Class f) {
                serviceIClasses.add(f);
            }

            @Override
            public void onGetNormalClass(Class f) {
                normalClasses.add(f);
            }

            @Override
            public void onGetClass(Class f) {
                allClasses.add(f);
            }
        });
    }


    //搜索该类涉及的所有class
    private static void searchAllClassWithCb(Class c, OnGetClass onGetClass) {
        if (null == c || isIgnoredClass(c)) {
            return;
        }
        try {
            onGetClass.onGetClass(c);
            String name = c.getName();
            if (name.contains(mapperClassSuffix) && c.isInterface()) {
                //mapper
                onGetClass.onGetMapperClass(c);
                return;
            } else {
                boolean isService = false;
                Class implClass = null;

                if (c.isInterface()) {
                    if (findImpClass != null)
                        implClass = findImpClass.findImplClass(c);//findServcieImplClass(c);
                } else {
                    implClass = c;
                }
                if (null == implClass) {
                    return;
                }
                if (null != implClass.getDeclaredAnnotation(Service.class)) {
                    isService = true;
                }
                if (isService) {
                    //sercice
                    onGetClass.onGetServiceClass(c);
                    //获取service实现类-普通类
                    onGetClass.onGetNormalClass(implClass);
                    //接口和实现类对应关系
                    serviceiAndImpl.put(c, implClass);
                } else {
                    //普通field
                    onGetClass.onGetNormalClass(implClass);
                }
                //递归处理
                Field[] fields = implClass.getDeclaredFields();
                for (Field f : fields) {
                    //基本类型 list map 不处理
                    if (isIgnoredClass(f.getType())) {
                        continue;
                    }
                    searchAllClassWithCb(f.getType(), onGetClass);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取field后的回调
    public interface OnGetClass {
        void onGetMapperClass(Class f);

        void onGetServiceClass(Class f);

        void onGetNormalClass(Class f);//非Mapper非Service

        void onGetClass(Class f);//每次获取field都会调用
    }

    //创建mapper
    public interface MapperFactory {
        <T extends Object> T create(HashSet<Class> allMapperClasses, Class<T> c);
    }

    //用于查询service实现类。有时候 service 和实现类不是一个。
    public interface FindImpClass {
        Class findImplClass(Class c);
    }

}
