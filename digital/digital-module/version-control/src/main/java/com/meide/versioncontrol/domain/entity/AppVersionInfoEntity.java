package com.meide.versioncontrol.domain.entity;

import com.meide.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * app版本控制
 */
@Data
public class AppVersionInfoEntity extends BaseEntity {

    /**
     * 版本id
     */
    private Long versionId;

    /**
     * 更新状态 强制更新或者自愿更新
     */
    private Integer updateStatus;

    /**
     * 版本号
     */
    private Integer versionCode;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 更新内容
     */
    private String modifyContent;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * apk大小
     */
    private Integer apkSize;

    /**
     * apk的签名校验值
     */
    private String apkSign;

    /**
     * app唯一标识
     */
    private String appKey;
}
