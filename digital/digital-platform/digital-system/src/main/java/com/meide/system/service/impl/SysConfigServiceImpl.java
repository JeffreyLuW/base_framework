package com.meide.system.service.impl;

import com.meide.common.annotation.DataSource;
import com.meide.common.constant.UserConstants;
import com.meide.common.core.cashe.CacheUtil;
import com.meide.common.core.text.Convert;
import com.meide.common.enums.CacheType;
import com.meide.common.enums.DataSourceType;
import com.meide.common.exception.CustomException;
import com.meide.common.utils.SecurityUtils;
import com.meide.common.utils.StringUtils;
import com.meide.system.domain.SysConfig;
import com.meide.system.mapper.SysConfigMapper;
import com.meide.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author jiay
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService {
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private CacheUtil cacheUtil;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList) {
            cacheUtil.setCacheObject(CacheType.SYS_CONFIG, config.getConfigKey(), config.getConfigValue());
        }
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public SysConfig selectConfigById(Long configId) {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(cacheUtil.getCacheObject(CacheType.SYS_CONFIG, configKey));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig)) {
            cacheUtil.setCacheObject(CacheType.SYS_CONFIG, configKey, retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 根据键名模糊查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public List<SysConfig> selectConfigByLeftLikeKey(String configKey) {
        //前缀必须以.结尾分组
        if (!configKey.endsWith(".")) {
            configKey += ".";
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        List<SysConfig> retConfig = configMapper.selectConfigByLeftLike(config);
        return retConfig;
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return configMapper.selectConfigList(config);
    }


    /**
     * 插入或者修改多个config配置。因为配置项不会太多，操作也不会频繁，这里没有用批量插入删除
     *
     * @param configs
     * @return
     */
    @Override
    @Transactional
    public int insertOrUpdateConfigs(List<SysConfig> configs) {
        if (null == configs || configs.isEmpty()) {
            return 0;
        }
        final String currUser = SecurityUtils.getUsername();
        int rows = 0;
        for (SysConfig config : configs) {
            if (null != config.getConfigId() && config.getConfigId() > 0) {
                if (UserConstants.NOT_UNIQUE.equals(checkConfigKeyUnique(config))) {
                    throw new CustomException("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
                }
                config.setUpdateBy(currUser);
                rows += configMapper.updateConfig(config);
            } else {
                if (UserConstants.NOT_UNIQUE.equals(checkConfigKeyUnique(config))) {
                    throw new CustomException("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
                }
                config.setCreateBy(currUser);
                config.setUpdateBy(currUser);
                rows += configMapper.insertConfig(config);
            }
        }
        if (rows > 0) {
            clearCache();
        }
        return rows;
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        int row = configMapper.insertConfig(config);
        if (row > 0) {
            cacheUtil.setCacheObject(CacheType.SYS_CONFIG, config.getConfigKey(), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {
        int row = configMapper.updateConfig(config);
        if (row > 0) {
            cacheUtil.setCacheObject(CacheType.SYS_CONFIG, config.getConfigKey(), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    @Override
    public int deleteConfigByIds(Long[] configIds) {
        int count = configMapper.deleteConfigByIds(configIds);
        if (count > 0) {
            clearCache();
        }
        return count;
    }

    /**
     * 清空缓存数据
     */
    @Override
    public void clearCache() {
        Collection<String> keys = cacheUtil.findAllByType(CacheType.SYS_CONFIG);
        for (String key : keys) {
            cacheUtil.deleteObject(CacheType.SYS_CONFIG, key);
        }
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }


}
