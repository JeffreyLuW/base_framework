package com.meide.system.service.impl;


import com.meide.common.exception.CustomException;
import com.meide.common.utils.DictUtil;
import com.meide.common.utils.SecurityUtils;
import com.meide.common.utils.SysDictDetail;
import com.meide.system.domain.SysTheme;
import com.meide.system.domain.param.sysTheme.SysThemePageParam;
import com.meide.system.domain.vo.ThemeVo;
import com.meide.system.mapper.SysThemeMapper;
import com.meide.system.service.ISysDictDataService;
import com.meide.system.service.ISysThemeService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主题(SysTheme)表服务实现类
 *
 * @author jiay
 */
@Service("sysThemeService")
public class SysThemeServiceImpl implements ISysThemeService {
    @Resource
    private SysThemeMapper sysThemeMapper;

    @Resource
    private ISysDictDataService sysDictDataService;

    /**
     * 通过ID查询单条数据
     *
     * @param key 主键
     * @return 实例对象
     */
    @Override
    public SysTheme info(String key) {
        SysTheme sysTheme = sysThemeMapper.selectByPrimaryKey(key);
        List<SysDictDetail> allDetail = sysDictDataService.findAllDetail();
        DictUtil.parseLabel(allDetail,sysTheme,"status","sys_normal_disable",null);
        return sysTheme;
    }

    /**
     * 查询多条数据
     *
     * @param pageParam 查询条件对象
     * @return 对象列表
     */
    @Override
    public List<SysTheme> page(SysThemePageParam pageParam) {
        PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getOrderBy());
        List<SysTheme> list = sysThemeMapper.selectByParam(pageParam);
        List<SysDictDetail> allDetail = sysDictDataService.findAllDetail();
        DictUtil.parseLabel(allDetail,list,"status","sys_normal_disable",null);
        return list;
    }

    /**
     * 新增数据
     *
     * @param sysTheme 新增对象
     * @return 插入数据数
     */
    @Override
    @CacheEvict(cacheNames = "sys_theme",allEntries = true)
    public int add(SysTheme sysTheme) {
        if (sysThemeMapper.selectByPrimaryKey(sysTheme.getKey()) != null) {
            throw new CustomException("主题编号已存在");
        }
        sysTheme.setCreateBy(SecurityUtils.getUsername());
        sysTheme.setCreateTime(new Date());
        return sysThemeMapper.insert(sysTheme);
    }

    /**
     * 修改数据
     *
     * @param sysTheme 修改对象
     * @return 实例对象
     */
    @Override
    @CacheEvict(cacheNames = "sys_theme",allEntries = true)
    public int edit(SysTheme sysTheme) {
        sysTheme.setUpdateBy(SecurityUtils.getUsername());
        sysTheme.setUpdateTime(new Date());
        return sysThemeMapper.updateByPrimaryKeySelective(sysTheme);
    }

    /**
     * 通过主键删除数据
     *
     * @param ids 待删除主键数组
     * @return 是否成功
     */
    @Override
    @CacheEvict(cacheNames = "sys_theme",allEntries = true)
    public int remove(String[] ids) {
        return sysThemeMapper.deleteByIds(ids);
    }

    @Override
    @Cacheable(cacheNames = "sys_theme")
    public List<ThemeVo> prepareConfig() {
        List<SysTheme> sysThemes = sysThemeMapper.selectAllEffective();
        if (sysThemes != null && sysThemes.size() > 0) {
            return sysThemes.stream().map(t -> {
                ThemeVo vo = new ThemeVo();
                BeanUtils.copyProperties(t, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return null;
    }
}