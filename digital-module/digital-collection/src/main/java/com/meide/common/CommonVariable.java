package com.meide.common;


import com.meide.model.PO.CollectionData;

import java.util.concurrent.ConcurrentHashMap;

public class CommonVariable {

    /**
     * 采集模块启动指令
     * */
    public static Integer COLLECTION_MODE = 0;

    /**
     * 分发模块启动指令
     * */
    public static Integer DISTRIBUTE_MODE = 0;

    /**
     * 历史数据模块启动指令
     * */
    public static Integer HISTORY_MODE = 0;

    /**
     * 告警/实时数据启动指令
     * */
    public static Integer ALERT_MODE = 0;

    /**
     * 采集数据
     * */
    public static ConcurrentHashMap<String, CollectionData> COMM_COL_MAP = new ConcurrentHashMap<>();



}
