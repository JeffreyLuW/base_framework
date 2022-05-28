package com.meide.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.meide.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一些常用工具
 */
public class UIDevUtil {

    public static HashMap<String, Object> apiError(String message) {
        return apiResult(500, message == null ? "操作失败" : message,null);
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
                    double doubleVal = Double.parseDouble(String.valueOf(obj));
                    val = (int) doubleVal;
                } catch (Exception ex) {
                    ex.printStackTrace();
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

    public static void main(String[] args) {
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(20);
        page.add(new MapHelper<>().put("name", "jiay").get());
        System.out.println(JsonUtils.toJson(page, true));
    }
}