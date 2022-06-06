package com.meide.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


/**
 * @author spark
 * @version 1.0
 * @date 2019/11/8 14:09
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		//log.info("数据源为"+DataSourceContextHolder.getDB());
		return DataSourceContextHolder.getDB();
	}
}

