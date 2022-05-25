package com.meide.system.domain.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class ListConfByKeyPrefix {
    @ApiModelProperty(value = "配置前缀", required = true, example = "page.")
    @NotEmpty(message = "前缀不能为空")
    private String configKey;

}
