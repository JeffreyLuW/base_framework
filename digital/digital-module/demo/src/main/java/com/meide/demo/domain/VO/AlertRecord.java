package com.meide.demo.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRecord {

    /**
     * 告警时间
     * */
    private Timestamp alertTs;

    /**
     * 告警规则id
     **/
    private Integer alertRuleId;

    /**
     * 规则名
     **/
    private String alertRuleName;

    /**
     * 告警阈值
     **/
    private String alertValue;

    /**
     * 运算符
     **/
    private Integer ruleOperator;

    /**
     * 指标id
     **/
    private String sensorId;

    /**
     * 指标值
     * */
    private Float sensorData;

}
