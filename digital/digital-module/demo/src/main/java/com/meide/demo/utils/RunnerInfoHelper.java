package com.meide.demo.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于获取当前请求流程中的各种数据
 */
public class RunnerInfoHelper {

    /**
     * 获取httprequest
     *
     * @return
     */
    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }



}
