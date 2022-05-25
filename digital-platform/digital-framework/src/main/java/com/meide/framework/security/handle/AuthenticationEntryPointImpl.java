package com.meide.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.meide.common.constant.HttpStatus;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.utils.ServletUtils;
import com.meide.common.utils.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 *
 * @author jiay
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        String requestURI = request.getRequestURI();

        if (ServletUtils.isAjaxRequest(request)) {
            int code = HttpStatus.UNAUTHORIZED;
            String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
            ServletUtils.renderString(response, 401, JSON.toJSONString(AjaxResult.error(code, msg)));

        } else if ("/error".equals(requestURI)) {
            int code = HttpStatus.UNAUTHORIZED;
            String msg = "静态文件不存在：/index.html";
            ServletUtils.renderString(response,404, JSON.toJSONString(AjaxResult.error(code, msg)));
        } else {
            response.setContentType("application/json");
            response.sendRedirect("/index.html");
        }

    }
}
