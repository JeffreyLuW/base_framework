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
public class DeviceGroup implements Serializable {

    /**
     *
     **/
    @TableId(type= IdType.AUTO)
    private Integer groupId;

    /**
      * 
      **/
    private Integer groupPid;

        /**
      * 
      **/
    private String groupName;

        /**
      * 
      **/
    private Integer groupSort;

        /**
      * 
      **/
    private String groupType;

    }