package com.meide.system.domain.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author jiay
 */
@Data
@Builder
@ApiModel(description = "验证码对象")
public class CaptchaResult {
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("描述信息")
    private String msg;
    @ApiModelProperty("验证码编号")
    private String uuid;
    @ApiModelProperty("图片Base64编码")
    private String img;
}
