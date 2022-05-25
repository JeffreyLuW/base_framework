package com.meide.dbengine.api;

import com.meide.common.exception.CustomException;
import com.meide.dbengine.factory.EngineFactory;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.utils.EngineUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 实际接口执行类。后期扩展环绕处理。
 * 这里指定了返回格式。没有实用统一的AjaxResult，目的是可能日后作为一个完全独立无其他依赖的模块。
 */
public class ApiRealRunner {

    private EngineFactory engineFactory;
    private BaseApi baseApi;


    public ApiRealRunner(EngineFactory engineFactory, BaseApi baseApi) {
        this.engineFactory = engineFactory;
        this.baseApi = baseApi;


    }

    @ResponseBody
    public Object call(@RequestBody HashMap params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RunningInfo runningInfo = new RunningInfo(request, response);
        runningInfo.setParams(params);
        runningInfo.setBaseApi(baseApi);
        Object rs = null;
        try {
            rs = engineFactory.getChainRunner().run(runningInfo);
            if (runningInfo.isPage()) {
                rs = page(runningInfo.getTotal(), rs);
            } else {
                rs = success(null, rs);
            }
        } catch (Exception ex) {
            if (ex instanceof CustomException) {
                rs = error(((CustomException) ex).getMessage(), null);
            } else {
                ex.printStackTrace();
                if (engineFactory.isDev()) {
                    rs = error("操作失败", EngineUtil.simpleExceptionDesc(ex));
                } else {
                    rs = error("操作失败", null);
                }
            }
        }
        return rs;
    }

    private Object page(long total, Object data) {
        HashMap map = new LinkedHashMap();
        map.put("code", 200);
        map.put("msg", "success");
        map.put("total", total);
        map.put("rows", data);
        return map;
    }

    private Object success(String msg, Object data) {
        HashMap map = new LinkedHashMap();
        map.put("code", 200);
        map.put("msg", msg == null ? "success" : msg);
        map.put("data", data);
        return map;
    }

    private Object error(String msg, List<String> ex) {
        HashMap map = new LinkedHashMap();
        map.put("code", 500);
        map.put("msg", msg == null ? "操作失败" : msg);
        if (null != ex) {
            map.put("ex", ex);
        }
        return map;
    }

}
