package com.meide.register.config;

import com.meide.register.config.properties.DataApiProperties;
import com.meide.register.converter.TableConverter;
import com.meide.register.converter.imp.DefaultTableConverter;
import com.meide.register.manager.DataApiManager;
import com.meide.register.service.IDataApiService;
import com.meide.register.service.impl.DataApiServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态注册统一crud接口配置类
 *
 * @author jiay
 */
@Configuration
@ConditionalOnProperty(name = "digital.data-api.enabled",matchIfMissing = false)
public class  DataApiConfiguration {

    //注册一个默认表名列名转化器
    @Bean
    @ConditionalOnMissingBean(TableConverter.class)
    public DefaultTableConverter passwordEncoder() {
        return new DefaultTableConverter();
    }


    @Bean
    public DataApiManager autoRegisterBeanManager(IDataApiService dataApiService, TableConverter  tableConverter, DataApiProperties dataApiProperties ) {
        return new DataApiManager(dataApiService,tableConverter,dataApiProperties);
    }

    @Bean
    public IDataApiService getDataApiService( ) {
        return new DataApiServiceImpl( );
    }


}
