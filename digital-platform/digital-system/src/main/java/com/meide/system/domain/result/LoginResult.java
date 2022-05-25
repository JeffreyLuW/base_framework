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
@ApiModel(description = "登录返回值")
public class LoginResult {
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("描述信息")
    private String msg;
    @ApiModelProperty("登录令牌")
    private String token;
    @ApiModelProperty("首页大屏")
    private Boolean bigscreen;
}
