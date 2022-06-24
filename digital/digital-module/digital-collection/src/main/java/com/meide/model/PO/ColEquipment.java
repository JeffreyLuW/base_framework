package com.meide.model.PO;


import java.io.Serializable;
import java.sql.Timestamp;

/**
* entity
* 
*/

public class ColEquipment implements Serializable {
    /**
      * 主键
      **/
    private String equipmentId;

        /**
      * 设备名
      **/
    private String equipmentName;

        /**
      * 型号
      **/
    private String equipmentModel;

        /**
      * 项目id
      **/
    private Integer equipmentProId;

        /**
      * 厂家
      **/
    private String equipmentFactory;

        /**
      * 安装位置
      **/
    private String equipemntAddr;

        /**
      * 监测点id
      **/
    private Integer jcdId;

        /**
      * 设备配置
      **/
    private String equipmentConfig;

        /**
      * 协议id
      **/
    private String protocolId;

    }