package com.meide.common;


public class FinalVariable {

    /**
     * redis采集集合
     * */
    public static String COLLECTION_DATA_KEY = "REDIS_COLLECTION_DATA";

    /**
     * redis历史数据集合
     * */
    public static String HISTORY_DATA_KEY = "REDIS_HISTORY_DATA";

    /**
     * redis实时报警数据
     * */
    public static String REALTIME_DATA_KEY = "REDIS_REALTIME_DATA";

    /**
     * 报警记录集合
     * */
    public static String ALERT_RECORDS_KEY = "REDIS_ALERT_RECORDS";

    /**
     * 一次性拿取redis采集数据长度
     * */
    public static int COLLECTION_DATA_LENGTH = 1000;

    /**
     * 分发模块线程数
     * */
    public static int DISTRIBUTE_THREAD_NUM = 1;




}
