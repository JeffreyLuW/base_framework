package com.meide.versioncontrol.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.meide.common.exception.CustomException;
import com.meide.common.utils.SecurityUtils;
import com.meide.common.utils.StringUtils;
import com.meide.system.domain.entity.SysFilelink;
import com.meide.system.mapper.SysFilelinkMapper;
import com.meide.versioncontrol.domain.dto.AppVersionInfoDTO;
import com.meide.versioncontrol.domain.entity.AppVersionInfoEntity;
import com.meide.versioncontrol.mapper.AppVersionInfoMapper;
import com.meide.versioncontrol.service.AppVersionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * app版本控制
 */
@Service
public class AppVersionInfoServiceImpl implements AppVersionInfoService {

    @Autowired
    private AppVersionInfoMapper appVersionInfoMapper;

    @Autowired
    private SysFilelinkMapper sysFilelinkMapper;

    /**
     * 保存版本信息
     *
     * @param dto
     * @return
     */
    @Override
    public int saveVersionInfo(AppVersionInfoDTO dto) {
        int num = 0;
        if (null != dto) {
            AppVersionInfoEntity queryEntity = new AppVersionInfoEntity();
            queryEntity.setAppKey(dto.getAppKey());
            queryEntity.setVersionCode(dto.getVersionCode());
            List list = appVersionInfoMapper.selectVersionList(queryEntity);
            if (CollUtil.isNotEmpty(list)) {
                throw new CustomException("版本号为" + dto.getVersionCode() + "已存在！");
            }

            if (StringUtils.isNotEmpty(dto.getDownloadUrl())) {
                SysFilelink sysFilelink = sysFilelinkMapper.selectById(dto.getDownloadUrl());
                if (null != sysFilelink) {
                    dto.setDownloadUrl(sysFilelink.getPath());
                } else {
                    throw new CustomException("apk下载地址不能为空");
                }
            } else {
                throw new CustomException("apk下载地址不能为空");
            }
            AppVersionInfoEntity entity = BeanUtil.copyProperties(dto, AppVersionInfoEntity.class);
            entity.setCreateBy(SecurityUtils.getUsername());
            entity.setCreateTime(new Date());
            num = appVersionInfoMapper.insertVersion(entity);
        } else {
            throw new CustomException("版本信息不能为空");
        }
        return num;
    }

    @Override
    public int updateVersionInfo(AppVersionInfoDTO dto) {
        int num = 0;
        if (null != dto) {
            AppVersionInfoEntity queryEntity = new AppVersionInfoEntity();
            queryEntity.setAppKey(dto.getAppKey());
            queryEntity.setVersionCode(dto.getVersionCode());
            List<AppVersionInfoEntity> list = appVersionInfoMapper.selectVersionList(queryEntity);
            if (CollUtil.isNotEmpty(list)) {
                if (list.size() > 1) {
                    throw new CustomException("版本号为" + dto.getVersionCode() + "已存在！");
                } else {
                    if (!list.get(0).getVersionCode().equals(dto.getVersionCode())) {
                        //说明改了，重复
                        throw new CustomException("版本号为" + dto.getVersionCode() + "已存在！");
                    }
                }
            }
            if (dto.getDownloadUrl().equals("1")) {
                dto.setDownloadUrl(null);
            } else {
                SysFilelink sysFilelink = sysFilelinkMapper.selectById(dto.getDownloadUrl());
                if (null != sysFilelink) {
                    dto.setDownloadUrl(sysFilelink.getPath());
                } else {
                    throw new CustomException("apk下载地址不能为空");
                }
            }

            AppVersionInfoEntity entity = BeanUtil.copyProperties(dto, AppVersionInfoEntity.class);
            entity.setUpdateBy(SecurityUtils.getUsername());
            entity.setUpdateTime(new Date());
            num = appVersionInfoMapper.updateVersion(entity);
        } else {
            throw new CustomException("版本信息不能为空");
        }
        return num;
    }

    /**
     * 删除版本信息
     *
     * @param ids
     * @return
     */
    @Override
    public int deleteVersionInfo(List<Long> ids) {
        int num = 0;
        if (CollUtil.isNotEmpty(ids)) {
            num = appVersionInfoMapper.deleteVersion(ids);
        }
        return num;
    }

    /**
     * 查询版本信息列表数据
     *
     * @param dto
     * @return
     */
    @Override
    public List<AppVersionInfoDTO> getAppVersionInfoList(AppVersionInfoDTO dto) {
        List<AppVersionInfoDTO> dtoList = new ArrayList<>(8);
        AppVersionInfoEntity queryEntity = new AppVersionInfoEntity();
        if (null != dto) {
            queryEntity = BeanUtil.copyProperties(dto, AppVersionInfoEntity.class);
        }
        List<AppVersionInfoEntity> list = appVersionInfoMapper.selectVersionList(queryEntity);
        if (CollUtil.isNotEmpty(list)) {
            dtoList = list.stream()
                    .map(item -> BeanUtil.copyProperties(item, AppVersionInfoDTO.class)).collect(Collectors.toList());
        }
        return dtoList;
    }

    /**
     * 根据主键查询版本信息
     *
     * @param versionId
     * @return
     */
    @Override
    public AppVersionInfoDTO getAppVersionInfoById(Long versionId) {
        AppVersionInfoEntity entity = appVersionInfoMapper.selectVersionById(versionId);
        return Optional.ofNullable(entity).map(item -> BeanUtil.copyProperties(item, AppVersionInfoDTO.class)).orElse(null);
    }

    /**
     * 根据appkey获取最新版本信息
     *
     * @param appkey
     * @return
     */
    @Override
    public AppVersionInfoDTO getAppNewVersionInfo(String appkey) {
        AppVersionInfoDTO dto = null;
        if (StringUtils.isNotEmpty(appkey)) {
            AppVersionInfoEntity entity = new AppVersionInfoEntity();
            entity.setAppKey(appkey);
            List<AppVersionInfoEntity> list = appVersionInfoMapper.selectVersionList(entity);
            if (CollUtil.isNotEmpty(list)) {
                entity = list.get(0);
                dto = BeanUtil.copyProperties(entity, AppVersionInfoDTO.class);
            }
        } else {
            throw new CustomException("APP KEY不能为空");
        }
        return dto;
    }
}
