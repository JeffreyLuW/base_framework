package com.meide.model.PO;


import java.io.Serializable;
import java.sql.Timestamp;

/**
* entity
* 
*/

public class AlertRule implements Serializable {
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
      * 是否启用
      **/
    private String isEnable;

    }