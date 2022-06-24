package com.meide.system.domain.param.sysFilelink;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * 业务——文件关联表(SysFilelink)修改参数
 *
 * @author jiay
 */
@Data
@ApiModel(description = "业务——文件关联表修改参数")
public class SysFilelinkEditParam {
    @ApiModelProperty(value = "关联id",required = true)
    @NotBlank(message = "关联id不能为空")
    private String groupId;
    @ApiModelProperty(value = "文件组内文件uri")
    private String[] ids;
}
