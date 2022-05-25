package com.meide.framework.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Security 配置属性
 *
 * @author jiay
 */

@Configuration
@ConfigurationProperties(prefix = "digital.security")
public class SecurityProperties
{

    /**
     * 无需认证的静态资源,以逗号","作为分隔符
     */
    private String ignoringStaticUrls  ;

    public String getIgnoringStaticUrls() {
        return ignoringStaticUrls;
    }

    public void setIgnoringStaticUrls(String ignoringStaticUrls) {
        this.ignoringStaticUrls = ignoringStaticUrls;
    }
}
