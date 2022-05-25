package com.meide.web.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 扫码登录传输对象
 */
@ApiModel("扫码登录传输对象")
@Data
public class ScanLoginDTO {

    @ApiModelProperty("客户端id")
    @NotBlank(message = "客户端id不能为空")
    private String clientId;

    @ApiModelProperty("时间戳")
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;

}
