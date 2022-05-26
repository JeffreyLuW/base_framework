package com.meide.common.config;

import com.meide.common.DynamicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {




	/**
	 * 	mysql数据源
	 */
	@Bean(name = "mysqldb")
	@ConfigurationProperties(prefix = "spring.datasource.master")
	public DataSource mysqlDataSource() {
		return DataSourceBuilder.create().build();

	}
	/**
	 * 	mysql数据源
	 */
	@Bean(name = "taosdb")
	@ConfigurationProperties(prefix = "spring.datasource.taosdb")
	public DataSource taosDataSource() {
		return DataSourceBuilder.create().build();
	}


	/**
	 * 动态数据源: 通过AOP在不同数据源之间动态切换
	 * @return
	 */
	@Primary
	@Bean(name = "dynamicDataSource")
	public DataSource dynamicDataSource() {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		// 默认数据源
		dynamicDataSource.setDefaultTargetDataSource(mysqlDataSource());
		// 配置多数据源
		Map<Object, Object> dsMap = new HashMap();
		dsMap.put("mysqldb", mysqlDataSource());
		dsMap.put("taosdb",taosDataSource());
		dynamicDataSource.setTargetDataSources(dsMap);
		return dynamicDataSource;
	}
	/**
	 * 配置@Transactional注解事物
	 * @return
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dynamicDataSource());
	}

}
