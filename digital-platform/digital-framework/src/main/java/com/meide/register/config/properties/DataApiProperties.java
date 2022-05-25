package com.meide.register.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Security 配置属性
 *
 * @author jiay
 */

@Configuration
@ConfigurationProperties(prefix = "digital.data-api")
public class DataApiProperties {

    /**
     * 屏蔽敏感表名,以逗号","作为分隔符
     */
    private String ignoredTableList = "";

    private String updateIgnoredColumn = "";


    public String getIgnoredTableList() {
        return ignoredTableList;
    }

    public void setIgnoredTableList(String ignoredTableList) {
        this.ignoredTableList = ignoredTableList;
    }

    public String getUpdateIgnoredColumn() {
        return updateIgnoredColumn;
    }

    public void setUpdateIgnoredColumn(String updateIgnoredColumn) {
        this.updateIgnoredColumn = updateIgnoredColumn;
    }
}
