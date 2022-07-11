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
public class CanvasSensorBind implements Serializable {

    /**
     * id
     **/
    private Integer id;

    /**
      * 组态id
      **/
    private Integer canvasId;

        /**
      * 变量id
      **/
    private String sensorId;

    }