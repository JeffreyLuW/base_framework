package com.meide.web.controller.system;

import java.util.List;

import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.PageResult;
import com.meide.common.enums.BusinessType;
import com.meide.system.domain.SysConfig;
import com.meide.system.domain.request.ListConfByKeyPrefix;
import com.meide.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.meide.common.annotation.Log;
import com.meide.common.annotation.RepeatSubmit;
import com.meide.common.constant.UserConstants;
import com.meide.common.utils.SecurityUtils;
import com.meide.common.utils.poi.ExcelUtil;

/**
 * 参数配置 信息操作处理
 *
 * @author jiay
 */
@RestController
@Api(tags = "系统参数")
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public PageResult list(SysConfig config) {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return page(list);
    }

    /**
     * 根据key前缀模糊查询
     *
     * @param config
     * @return
     */
    @PostMapping("/listLike")
    @ApiOperation("查询参数配置|keyPrefix")
    public AjaxResult<List<SysConfig>> listByKeyPrefix(@Validated @RequestBody ListConfByKeyPrefix config) {
        List<SysConfig> list = configService.selectConfigByLeftLikeKey(config.getConfigKey());
        return AjaxResult.success(list);
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @GetMapping("/export")
    public AjaxResult export(SysConfig config) {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        return util.exportExcel(list, "参数数据");
    }


    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId) {
        return AjaxResult.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey) {
        return AjaxResult.successWithMsg(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    @RepeatSubmit
    public AjaxResult add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return AjaxResult.errorWithMsg("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(SecurityUtils.getUsername());
        return rows(configService.insertConfig(config));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasAnyPermi('system:config:add,system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping("/addOrUpdate")
    @RepeatSubmit
    @ApiOperation("参数批量新增修改")
    public AjaxResult addOrUpdate(@Validated @RequestBody List<SysConfig> configs) {
        return rows(configService.insertOrUpdateConfigs(configs));
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return AjaxResult.errorWithMsg("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(SecurityUtils.getUsername());
        return rows(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        return rows(configService.deleteConfigByIds(configIds));
    }

    /**
     * 清空缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clearCache")
    public AjaxResult clearCache() {
        configService.clearCache();
        return success();
    }
}
