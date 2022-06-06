package com.meide.common;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DataSourceContextHolder {

	/**
	 * 默认数据源
	 */

	public static final String MYSQLDB = "mysqldb";
	public static final String TAOSDB = "taosdb";

	private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

	// 设置数据源名
	public static void setDB(String dbType) {
		//log.info("切换到{"+dbType+"}数据源");
		contextHolder.set(dbType);

	}

	// 获取数据源名
	public static String getDB() {
		return (contextHolder.get());

	}

	// 清除数据源名
	public static void clearDB() {
		contextHolder.remove();

	}

}

