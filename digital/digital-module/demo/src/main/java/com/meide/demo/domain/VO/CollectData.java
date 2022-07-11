package com.meide.demo.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectData {

    /**
     * 设备id
     **/
    private String equipmentId;

    /**
     * 设备名
     **/
    private String equipmentName;

    /**
     * 指标id
     **/
    private String sensorId;

    /**
     * 指标名
     **/
    private String sensorName;

    /**
     * 指标采集数据
     * */
    private Float sensorData;

}
