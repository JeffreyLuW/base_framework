package com.meide.system.domain.param.sysTheme;

import com.meide.common.core.page.PageDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * (SysTheme)分页查询条件
 *
 * @author jiay
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页条件查询类")
public class SysThemePageParam extends PageDomain {
    @ApiModelProperty("状态(1停用,2正常)")
    private String status;
}