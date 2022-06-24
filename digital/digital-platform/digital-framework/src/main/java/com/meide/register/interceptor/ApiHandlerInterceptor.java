package com.meide.register.interceptor;
import com.alibaba.fastjson.JSONObject;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.utils.ServletUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ApiHandlerInterceptor implements HandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        StringBuffer url = request.getRequestURL();
        String contextPath = request.getServletContext().getContextPath();

        String  req=url+"/"+contextPath;
        if(req.contains("APIdelete")){
            AjaxResult ajaxResult = AjaxResult.errorWithMsg("访问APIdelete接口权限不足");
            ServletUtils.renderString(response, JSONObject.toJSONString(ajaxResult));
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
