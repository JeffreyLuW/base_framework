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
@ApiModel(description = "文件上传返回值")
public class FileUploadResult {
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("描述信息")
    private String msg;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("文件路径")
    private String url;
    @ApiModelProperty("文件id")
    private String id;
}
