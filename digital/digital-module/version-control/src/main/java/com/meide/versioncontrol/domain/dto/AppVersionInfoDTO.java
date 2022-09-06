package com.meide.versioncontrol.domain.dto;

import com.meide.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * app版本控制
 */
@Data
@ApiModel("app版本控制传输对象")
public class AppVersionInfoDTO extends BaseEntity {

    /**
     * 版本id
     */
    @ApiModelProperty("主键")
    private Long versionId;

    /**
     * 更新状态 强制更新或者自愿更新
     */
    @ApiModelProperty("更新状态")
    private Integer updateStatus;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    @NotNull(message = "版本号不能为空")
    private Integer versionCode;

    /**
     * 版本名称
     */
    @ApiModelProperty("版本名称")
    @NotNull(message = "版本名称不能为空")
    private String versionName;

    /**
     * 更新内容
     */
    @ApiModelProperty("更新内容")
    @NotNull(message = "更新内容不能为空")
    private String modifyContent;

    /**
     * 下载地址
     */
    @ApiModelProperty("下载地址")
    @NotNull(message = "下载地址不能为空")
    private String downloadUrl;

    /**
     * apk大小
     */
    @ApiModelProperty("apk大小")
    private Integer apkSize;

    /**
     * apk的签名校验值
     */
    @ApiModelProperty("签名校验值")
    private String apkSign;

    /**
     * app唯一标识
     */
    @ApiModelProperty("app唯一编码")
    @NotNull(message = "app标识不能为空")
    private String appKey;
}
