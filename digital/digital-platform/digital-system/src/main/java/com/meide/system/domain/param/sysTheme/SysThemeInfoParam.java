package com.meide.system.domain.param.sysTheme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "主题请求参数")
public class SysThemeInfoParam {
    @ApiModelProperty(value = "主键",required = true)
    @NotBlank(message = "主题编号不能为空")
    @Size(max = 50,message = "主题编号需少于50字符")
    private String key;
    @ApiModelProperty(value = "主题名称",required = true)
    @NotBlank(message = "主题名称不能为空")
    @Size(max = 255,message = "主题名称需少于255字符")
    private String name;
    @ApiModelProperty(value = "主题变量",required = true)
    @NotBlank(message = "主题变量不能为空")
    @Size(max = 2000,message = "主题变量需少于2000字符")
    private String vars;
    @ApiModelProperty("备注说明")
    @Size(max = 255,message = "主题备注需少于255字符")
    private String remark;
    @ApiModelProperty(value = "状态 sys_normal_disable",required = true)
    @NotBlank(message = "主题状态不能为空")
    private String status;
}
