package com.meide.versioncontrol.service;

import com.meide.versioncontrol.domain.dto.AppVersionInfoDTO;

import java.util.List;

/**
 * app版本控制
 */
public interface AppVersionInfoService {

    /**
     * 保存版本信息
     *
     * @param dto
     * @return
     */
    int saveVersionInfo(AppVersionInfoDTO dto);

    /**
     * 更新版本信息
     *
     * @param dto
     * @return
     */
    int updateVersionInfo(AppVersionInfoDTO dto);

    /**
     * 删除版本信息
     *
     * @param ids
     * @return
     */
    int deleteVersionInfo(List<Long> ids);

    /**
     * 获取版本信息列表
     *
     * @param dto
     * @return
     */
    List<AppVersionInfoDTO> getAppVersionInfoList(AppVersionInfoDTO dto);

    /**
     * 根据主键获取版本信息
     *
     * @param versionId
     * @return
     */
    AppVersionInfoDTO getAppVersionInfoById(Long versionId);

    /**
     * 获取app最新版本信息
     *
     * @param appkey
     * @return
     */
    AppVersionInfoDTO getAppNewVersionInfo(String appkey);
}
