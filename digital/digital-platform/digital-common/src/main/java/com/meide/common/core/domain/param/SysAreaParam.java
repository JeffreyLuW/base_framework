package com.meide.common.core.domain.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 操作消息提醒
 *
 * @author jiay
 */
@Data
@ApiModel("行政区划下拉框参数")
public class SysAreaParam {

    @ApiModelProperty("上级行政区划")
    private Long parentCode = 370000L;
    @ApiModelProperty("行政层级 1省2市3县4镇5村")
    private int level = 1;

}
