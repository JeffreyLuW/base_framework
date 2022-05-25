package com.meide.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "主题返回值")
public class ThemeVo {
    @ApiModelProperty("主题编号")
    private String key;
    @ApiModelProperty("主题名称")
    private String name;
    @ApiModelProperty("主题变量")
    private String vars;
}
