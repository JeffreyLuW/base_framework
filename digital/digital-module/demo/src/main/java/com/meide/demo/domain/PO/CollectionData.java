package com.meide.demo.domain.PO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * 采集上来的数据
 * */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionData {

    /**
     * 设备id
     * */
    String equipmentId;

    /**
     * 指标id
     * */
    String sensorId;

    /**
     * 指标数据
     * */
    Float sensorData;

    /**
     * 采集时间
     * */
    Long collectTs;

    /**
     * 是否报警
     * */
    String isAlert = "0";

    // 如果缓存拿不到该指标数据或者传感器数据不一致，就更新
    public boolean equals(CollectionData that) {
        if (this == that) return true;
        if (ObjectUtils.isEmpty(that) || getClass() != that.getClass()) return false;
        return Objects.equals(that.getSensorData(), this.getSensorData());
    }
}
