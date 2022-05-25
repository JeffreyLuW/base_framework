package com.meide.dbengine.runninginfo;

import com.meide.dbengine.api.BaseApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 独立抽象出运行时的所有信息
 */
public class RunningInfo {
    /**
     * http请求的参数。
     */
    private Map<String, Object> params;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private BaseApi baseApi;

    /**
     * 标记当前是否是分页接口 分页接口需要返回total
     */
    private boolean isPage;
    /**
     * 分页时有效。
     */
    private long total;

    public RunningInfo(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public BaseApi getBaseApi() {
        return baseApi;
    }

    public void setBaseApi(BaseApi baseApi) {
        this.baseApi = baseApi;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean page) {
        isPage = page;
    }

    public long getTotal() {
        return total;
    }

    //设置分页时，默认为分页接口。
    public void setTotal(long total) {
        this.total = total;
        this.isPage = true;
    }
}
