package com.meide.framework.config;

import com.meide.common.config.DigitalConfig;
import com.meide.common.constant.Constants;
import com.meide.common.utils.DistUtil;
import com.meide.framework.interceptor.RepeatSubmitInterceptor;
import com.meide.register.interceptor.ApiHandlerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 通用配置
 *
 * @author jiay
 */
@Configuration
@Slf4j
public class ResourcesConfig implements WebMvcConfigurer {
    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    @Autowired
    private ApiHandlerInterceptor apiHandlerInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*前端dist目录*/
        File distFile = DistUtil.findFileInWorkDir("digital-ui/dist", "ui", "view", "dist");
        if (null != distFile) {
            registry.addResourceHandler("/**").addResourceLocations("file:" + distFile.getAbsolutePath() + "/");
            log.info("===静态文件目录 : {} ===", distFile.getAbsolutePath());
        } else {
            log.info("===未读取到静态文件目录===");
        }
        /** 本地文件上传路径 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**").addResourceLocations("file:" + DigitalConfig.getAbsoluteProfilePath() + "/");
        /** swagger配置 */
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/static/swagger/");
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
        registry.addInterceptor(apiHandlerInterceptor).addPathPatterns("/dataApi/**");
    }


    @Bean
    public ApiHandlerInterceptor apiHandlerInterceptor() {
        return new ApiHandlerInterceptor();
    }


    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOrigin("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
