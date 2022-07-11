package com.meide.demo.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectConfig extends CollectData{


    /**
     * 设备id
     **/
    private String equipmentId;

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
     * 设备名
     **/
    private String equipmentName;

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
     * 变量id
     **/
    private String sensorId;

    /**
     * 变量数据类型
     **/
    private Integer sensorDataType;

    /**
     * 变量描述
     **/
    private String sensorDesc;

    /**
     * 变量名
     **/
    private String sensorName;

    /**
     * 寄存器地址
     **/
    private String sensorRegister;

    /**
     * 单位
     **/
    private String sensorUnit;


    /** 以下字段为旧版灵活配置时所需，已弃用 */
    ///////////////////////////////////////////////////////////////////////////////

//    /**
//     * 设备id
//     **/
//    private String equipmentId;
//
//    /**
//     * 设备名
//     **/
//    private String equipmentName;
//
//    /**
//     * 指标id
//     **/
//    private String sensorId;
//
//    /**
//     * 指标名
//     **/
//    private String sensorName;

    // 以上为推送数据用，以下为采集用

    /**
     * 从站
     * */
    String device;

    /**
     * 寄存器
     * */
    String register;

    /**
     * 解析类型
     */
    String analyse;

    /**
     * 心跳包
     * */
    String heart;

    /**
     * 设备传感器
     * */
    String start;

    /**
     * 长度
     * */
    String length;

    /**
     * 单位
     * */
    String unit;


    /**
     * 报文依靠此属性解析出多个指标数据
     * */
    List<CollectConfig> childConfig = new ArrayList<>();

}
