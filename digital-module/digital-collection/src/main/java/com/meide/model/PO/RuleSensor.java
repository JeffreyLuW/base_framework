package com.meide.model.PO;


import java.io.Serializable;
import java.sql.Timestamp;

/**
* entity
* 
*/

public class RuleSensor implements Serializable {
    /**
      * 报警规则id
      **/
    private Integer alertRuleId;

        /**
      * 指标id
      **/
    private String sensorId;

        /**
      * 抑制报警至
      **/
    private String pauseTime;

        /**
      * 是否启用
      **/
    private String isEnable;

    }