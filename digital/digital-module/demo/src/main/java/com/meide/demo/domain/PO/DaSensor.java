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
public class DaSensor implements Serializable {

    /**
     * 变量id
     **/
    @TableId(type = IdType.ASSIGN_ID)
    private String sensorId;

    /**
     * 设备id
     **/
    private String equipmentId;

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

    }