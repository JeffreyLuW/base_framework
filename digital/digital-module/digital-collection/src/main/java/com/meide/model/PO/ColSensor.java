package com.meide.model.PO;


import java.io.Serializable;
import java.sql.Timestamp;

/**
* entity
* 
*/

public class ColSensor implements Serializable {
    /**
      * 指标id
      **/
    private String sensorId;

        /**
      * 指标名
      **/
    private Integer sensorName;

        /**
      * 设备id
      **/
    private Integer equipmentId;

        /**
      * 设备名
      **/
    private String equipmentName;

        /**
      * 指标配置
      **/
    private String sensorConfig;

    }