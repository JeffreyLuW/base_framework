package com.meide.system.service;


import com.meide.system.domain.SysTheme;
import com.meide.system.domain.param.sysTheme.SysThemePageParam;
import com.meide.system.domain.vo.ThemeVo;

import java.util.List;

/**
 * 主题(SysTheme)表服务接口
 *
 * @author jiay
 */
public interface ISysThemeService {

    /**
     * 通过ID查询单条数据
     *
     * @param key 主键
     * @return 实例对象
     */
    SysTheme info(String key);

    /**
     * 查询多条数据
     *
     * @param pageParam 查询条件对象
     * @return 对象列表
     */
    List<SysTheme> page(SysThemePageParam pageParam);

    /**
     * 新增数据
     *
     * @param  sysTheme 新增对象
     * @return 实例对象
     */
    int add(SysTheme sysTheme);

    /**
     * 修改数据
     *
     * @param  sysTheme 修改对象
     * @return 实例对象
     */
    int edit(SysTheme sysTheme);

    /**
     * 通过主键删除数据
     *
     * @param ids 主键数组
     * @return 是否成功
     */
    int remove(String[] ids);

    /**
     * 查询所有有效主题
     * @return 主题列表
     */
    List<ThemeVo> prepareConfig();

}