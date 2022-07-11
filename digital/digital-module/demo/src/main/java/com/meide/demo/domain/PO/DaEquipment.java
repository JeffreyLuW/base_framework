package com.meide.demo.domain.PO;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class DaEquipment implements Serializable {

    /**
     * 设备id
     **/
    @TableId(type = IdType.ASSIGN_ID)
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

    }