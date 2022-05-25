package com.meide.common.core.domain.entity;

import com.meide.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Date: 2021/12/3 14:36
 * @Author: jiay
 * @Description: APP菜单管理
 * @Version 1.0
 */
@Data
public class AppMenu extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long itemId;

    /**
     * 菜单名称
     */
    @ApiModelProperty("菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    /**
     * 菜单名称
     */
    @ApiModelProperty("菜单编码")
    @NotBlank(message = "菜单编码不能为空")
    private String code;
    /**
     * 上级id
     */
    @ApiModelProperty("上级id")
    private Long parentId;
    /**
     * 排序号
     */
    @ApiModelProperty("排序号")
    @NotNull(message = "排序号不能为空")
    private Integer orderNum;
    /**
     * 菜单类型
     */
    @ApiModelProperty("菜单类型（M目录 C菜单）")
    private String menuType;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String icon;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String desc;

    /**
     * 可见状态
     */
    @ApiModelProperty("可见状态（0显示 1隐藏）")
    @NotBlank(message = "可见状态不能为空")
    private String visible;
    /**
     * 使用状态
     */
    @ApiModelProperty("使用状态（0正常 1停用）")
    @NotBlank(message = "使用状态不能为空")
    private String status;

    @ApiModelProperty("子节点")
    private List<AppMenu> children;
}
