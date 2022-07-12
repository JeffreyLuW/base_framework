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
public class GroupDeviceBind implements Serializable {
    /**
     * id
     **/
    @TableId(type=IdType.AUTO)
    private Integer id;

    /**
      * 设备/变量id
      **/
    private Integer deviceId;

        /**
      * 类型（1：设备，2：变量
      **/
    private String deviceType;

        /**
      * 设备/变量组id
      **/
    private Integer groupId;

    }