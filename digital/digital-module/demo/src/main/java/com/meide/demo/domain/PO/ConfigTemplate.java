package com.meide.demo.domain.PO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* entity
* 
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigTemplate implements Serializable {

    /**
     * 模板id
     **/
    private Integer templateId;

    /**
      * 设备描述
      **/
    private String equipmentDesc;

        /**
      * IP/COM
      **/
    private String equipmentHost;

        /**
      * 链路类型
      **/
    private String equipmentLink;

        /**
      * 通信端口
      **/
    private String equipmentPort;

        /**
      * 设备协议
      **/
    private Integer equipmentProtocol;

        /**
      * 终端标识
      **/
    private String equipmentSign;

        /**
      * 设备类型
      **/
    private Integer equipmentType;

        /**
      * 变量数据类型
      **/
    private Integer sensorDataType;

        /**
      * 变量描述
      **/
    private String sensorDesc;

        /**
      * 寄存器地址
      **/
    private String sensorRegister;

        /**
      * 单位
      **/
    private String sensorUnit;

        /**
      * 模板名
      **/
    private String templateName;

        /**
      * 模板类型
      **/
    private String templateType;

    }