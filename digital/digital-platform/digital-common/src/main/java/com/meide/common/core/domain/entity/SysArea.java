package com.meide.common.core.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2021/12/3
 * @Author: jiay
 */
@Data
public class SysArea implements Serializable {
    private static final long serialVersionUID = 1L;

    //行政区划编码
    private Long code;
    //行政区划名称
    private String name;
    //上级行政区划
    private Long parentCode = 370000L;
    //行政层级 1省2市3县4镇5村
    private int level = 1;
    /** 子部门 */
    private List<SysArea> children = new ArrayList<>();

}
