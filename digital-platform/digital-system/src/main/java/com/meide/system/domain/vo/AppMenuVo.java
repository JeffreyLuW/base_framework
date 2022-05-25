package com.meide.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * APP菜单传输对象
 * @author jiay
 */
@Data
@ApiModel("APP菜单")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AppMenuVo {

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long itemId;

    /**
     * 菜单名称
     */
    @ApiModelProperty("菜单名称")
    private String name;

    /**
     * 菜单编码
     */
    @ApiModelProperty("菜单编码")
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
    private Integer orderNum;
    /**
     * 菜单类型
     */
    @ApiModelProperty("菜单类型")
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
     * 子级
     */
    @ApiModelProperty("子级节点")
    private List<AppMenuVo> children;
}
