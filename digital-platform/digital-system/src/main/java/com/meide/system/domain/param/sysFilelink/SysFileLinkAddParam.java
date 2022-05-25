package com.meide.system.domain.param.sysFilelink;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author jiay
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "业务——文件关联表新增参数")
public class SysFileLinkAddParam {

    @ApiModelProperty(value = "文件id拼成的字符串")
    @NotEmpty(message = "请上传相关文件")
    private String[] ids;

}
