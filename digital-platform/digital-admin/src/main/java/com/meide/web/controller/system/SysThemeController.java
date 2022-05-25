package com.meide.web.controller.system;


import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.PageResult;
import com.meide.system.domain.SysTheme;
import com.meide.system.domain.param.sysTheme.SysThemeInfoParam;
import com.meide.system.domain.param.sysTheme.SysThemePageParam;
import com.meide.system.service.ISysThemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 主题(SysTheme)控制层
 *
 * @author jiay
 */
@Api(tags = "主题控制器")
@RestController
public class SysThemeController extends BaseController {

    @Resource
    private ISysThemeService sysThemeService;

    @PreAuthorize("@ss.hasPermi('system:theme:list')")
    @GetMapping("/system/themes")
    @ApiOperation("分页查询")
    public PageResult<SysTheme> list(SysThemePageParam param) {
        return page(sysThemeService.page(param));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param key 主键
     * @return 单条数据
     */
    @PreAuthorize("@ss.hasPermi('system:theme:info')")
    @GetMapping("/system/theme/{key}")
    @ApiOperation("根据key查询信息")
    public AjaxResult<SysTheme> info(@ApiParam(value = "主键",required = true) @PathVariable String key) {
        return AjaxResult.success(this.sysThemeService.info(key));
    }

    /**
     * 新增主题
     *
     * @param param 新增数据
     * @return 成功条数
     */
    @PreAuthorize("@ss.hasPermi('system:theme:add')")
    @PostMapping("/system/theme")
    @ApiOperation("新增主题")
    public AjaxResult<Integer> add(@RequestBody @Validated SysThemeInfoParam param) {
        SysTheme entity = new SysTheme();
        BeanUtils.copyProperties(param,entity);
        return rows(sysThemeService.add(entity));
    }

    /**
     * 新增主题
     *
     * @param param 新增数据
     * @return 成功条数
     */
    @PreAuthorize("@ss.hasPermi('system:theme:edit')")
    @PutMapping("/system/theme")
    @ApiOperation("修改主题")
    public AjaxResult<Integer> edit(@RequestBody @Validated SysThemeInfoParam param) {
        SysTheme entity = new SysTheme();
        BeanUtils.copyProperties(param,entity);
        return rows(sysThemeService.edit(entity));
    }

    /**
     * 删除主题
     *
     * @param keys 删除主题的主键数组
     * @return 删除条数
     */
    @PreAuthorize("@ss.hasPermi('system:theme:remove')")
    @DeleteMapping("/system/theme/{keys}")
    @ApiOperation("删除主题")
    public AjaxResult<Integer> remove(@PathVariable @ApiParam(value = "待删除主题的编号的数组") String[] keys) {
        return rows(sysThemeService.remove(keys));
    }

}