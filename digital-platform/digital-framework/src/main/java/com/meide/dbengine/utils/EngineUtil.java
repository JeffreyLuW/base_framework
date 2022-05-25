package com.meide.dbengine.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Closeable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * 一些常用工具
 */
public class EngineUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static HashMap<String, Object> apiError(String message) {
        return apiResult(500, message == null ? "操作失败" : message, null);
    }

    public static HashMap<String, Object> apiSuccess(String message, Object data) {
        return apiResult(200, message == null ? "success" : message, data);
    }

    public static HashMap<String, Object> apiResult(int code, String message, Object data) {
        HashMap map = new HashMap();
        map.put("code", code);
        map.put("message", message);
        map.put("data", data);
        return map;
    }

    //获取db类型。
    public static String getDbType(String url) {
        if (url.contains("jdbc:oracle:")) {
            return "oracle";
        }
        if (url.contains("jdbc:mysql:")) {
            return "mysql";
        }
        if (url.contains("jdbc:sqlserver:")) {
            return "sqlserver";
        }
        return null;
    }

    //从url中 获取url
    public static String getDbName(String url) {
        int pSpliteIndex = url.indexOf("?");
        if (pSpliteIndex > -1) {
            url = url.substring(0, pSpliteIndex);
        }
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

    //如果result是List<Map>或者Map，将key转换为小写
    //注意Arrays.asList()的对象不可变，不能修改。
    public static Object mapKeyToLower(Object result) {
        if (result == null) {
            return result;
        }
        if (result instanceof List) {
            List list = (List) result;
            List tmp = new ArrayList<>(list);
            try {
                list.clear();//个别list不可改，只能new一个。
            } catch (Exception ex) {
//                ex.printStackTrace();
                result = list = new ArrayList<>();
            }
            for (Object o : tmp) {
                list.add(mapKeyToLower(o));
            }
        } else if (result instanceof Map) {
            Map map = (Map) result;
            Map tmp = new HashMap<>(map);
            map.clear();
            for (Object key : tmp.keySet()) {
                map.put(String.valueOf(key).toLowerCase(), tmp.get(key));
            }
        }
        return result;
    }

    //拼接两个路径 自动处理前后/的问题
    public static String joinUrl(String module_path, String subUrl) {
        if (null == module_path) {
            module_path = "";
        }
        if (null == subUrl) {
            subUrl = "";
        }
        module_path = module_path.trim();
        subUrl = subUrl.trim();
        //修正moduleath --> /hello/test
        if (!module_path.startsWith("/")) {
            module_path = "/" + module_path;
        }
        if (module_path.endsWith("/")) {
            module_path = module_path.substring(0, module_path.length() - 1);
        }
        if (!subUrl.startsWith("/")) {
            subUrl = "/" + subUrl;
        }

        return module_path + subUrl;
    }

    //强转int
    public static Integer parseInt(Object obj, Integer defaultVal) {
        Integer val = null;
        if (obj != null) {
            if (obj instanceof Integer) {
                val = (Integer) obj;
            } else {
                try {
                    String s = String.valueOf(obj);
                    if (!"null".equals(s) && !"".equals(s)) {
                        double doubleVal = Double.parseDouble(s);
                        val = (int) doubleVal;
                    }
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
        }
        return val == null ? defaultVal : val;
    }

    /**
     * 如果结果是分页Page对象，则提取对应分页信息返回
     *
     * @param rs
     * @return
     */
    public static Object transformPageIfNeeded(Object rs) {
        if (null != rs && rs instanceof Page) {
            PageInfo pageInfo = new PageInfo((Page) rs);
            HashMap pageMap = new HashMap();
            pageMap.put("pageNum", pageInfo.getPageNum());
            pageMap.put("pageSize", pageInfo.getPageSize());
            pageMap.put("pages", pageInfo.getPages());
            pageMap.put("total", pageInfo.getTotal());
            pageMap.put("list", pageInfo.getList());
            rs = pageMap;
        }
        return rs;
    }

    /**
     * 执行原生sql查询。
     *
     * @param sqlSessionFactory
     * @return
     */
    public static List<Map<String, Object>> exeQuerySql(SqlSessionFactory sqlSessionFactory, String sql) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection conn = null;
        Statement statement = null;
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            conn = sqlSession.getConnection();
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String colLabel = metaData.getColumnLabel(i);
                    Object object = rs.getObject(colLabel);
                    map.put(colLabel.toLowerCase(), object);
                }
                list.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAutoCloseable(statement);
            closeAutoCloseable(conn);
            closeAutoCloseable(sqlSession);
        }
        return list;
    }

    public static void closeAutoCloseable(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void closeAutoCloseable(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    ///获取某个对象的bool值。 数字1 bool:true 字符串true 均作为true。
    public static boolean boolValue(Object val) {
        if (val == null) {
            return false;
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        String pkStr = val.toString().toLowerCase();
        return "1".equals(pkStr) || "ok".equals(pkStr) || "yes".equals(pkStr) || "true".equals(pkStr);
    }

    public static String strVal(Object table_name) {
        return null == table_name ? "" : table_name.toString();
    }

    public static void main(String[] args) {
        System.out.println(parseInt("", 10));
    }

    public static Object formatMapperResult(Object result) {
        if (null == result) {
            return result;
        }
        if (result instanceof Map) {
            Map<Object, Object> map = (Map) result;
            for (Map.Entry entry : map.entrySet()) {
                Object val = entry.getValue();
                if (val == null || !(val instanceof Timestamp)) {
                    continue;
                }
                Timestamp tv = (Timestamp) val;
                map.put(entry.getKey(), simpleDateFormat.format(new Date(tv.getTime())));
            }
        } else if (result instanceof List) {
            List list = (List) result;
            list.stream().forEach(item -> formatMapperResult(item));
        }
        return result;
    }

    public static List<String> simpleExceptionDesc(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        List<String> list = new ArrayList<>();
        list.add(e.toString());
        for (int i = 0; i < stackTrace.length; i++) {
            if (i < 5) {
                list.add(stackTrace[i].toString());
            } else if (stackTrace[i].getClassName().startsWith("com.meide")) {
                list.add(stackTrace[i].toString());
            }
        }
        return list;
    }

}