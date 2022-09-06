package com.meide.versioncontrol.controller;

import com.meide.common.annotation.Log;
import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.PageResult;
import com.meide.system.service.ISysConfigService;
import com.meide.versioncontrol.domain.dto.AppVersionInfoDTO;
import com.meide.versioncontrol.service.AppVersionInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * app版本控制
 */
@RestController
@RequestMapping("appVersion")
@Api(tags = "手机app版本控制")
public class AppVersionInfoController extends BaseController {

    @Autowired
    private AppVersionInfoService appVersionInfoService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 获取版本信息列表
     *
     * @param dto
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("版本列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
    })
    public PageResult<AppVersionInfoDTO> list(AppVersionInfoDTO dto) {
        startPage();
        List<AppVersionInfoDTO> list = appVersionInfoService.getAppVersionInfoList(dto);
        return page(list);
    }

    @PostMapping("/add")
    @ApiOperation("保存版本信息")
    public AjaxResult save(@Validated @RequestBody AppVersionInfoDTO dto) {
        return AjaxResult.success(appVersionInfoService.saveVersionInfo(dto));
    }

    @PutMapping("/update")
    @ApiOperation("更新版本信息")
    public AjaxResult update(@Validated @RequestBody AppVersionInfoDTO dto) {
        return AjaxResult.success(appVersionInfoService.updateVersionInfo(dto));
    }

    @DeleteMapping("/{ids}")
    @ApiOperation("删除版本信息")
    @Log
    public AjaxResult delete(@PathVariable Long[] ids) {
        List<Long> list = Arrays.asList(ids.clone());
        return AjaxResult.success(appVersionInfoService.deleteVersionInfo(list));
    }

    @GetMapping("/info/{id}")
    @ApiOperation("获取版本详细信息")
    public AjaxResult<AppVersionInfoDTO> info(@PathVariable("id") Long id) {
        return AjaxResult.success(appVersionInfoService.getAppVersionInfoById(id));
    }

    @GetMapping("/newVersion")
    @ApiOperation("获取版本最新信息")
    public AjaxResult<AppVersionInfoDTO> getNewVersion(String appkey) {
        return AjaxResult.success(appVersionInfoService.getAppNewVersionInfo(appkey));
    }


    @GetMapping("/qrcode")
    @ApiOperation("获取二维码信息")
    public AjaxResult<String> getAppQrcode(String key) {
        return AjaxResult.success(configService.selectConfigByKey(key));
    }


}
