package com.meide.common.core.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Date: 2021/12/3 15:31
 * @Author: jiay
 * @Description: APP用户菜单
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("APP用户菜单")
public class AppUserMenu {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("菜单id")
    private Long appMenuId;

    @ApiModelProperty("排序号")
    private Integer orderNum;
}
