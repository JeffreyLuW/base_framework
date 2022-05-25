package com.meide.web.controller.system;

import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.entity.SysArea;
import com.meide.common.core.domain.param.SysAreaParam;
import com.meide.system.service.ISysAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/area")
@Api(tags = "行政区划信息控制器")
public class SysAreaController {

    @Autowired
    private ISysAreaService areaService;

    /**
     * 获取行政区划下拉树列表
     */
    @GetMapping("/treeSelect")
    @ApiOperation(value = "获取行政区划下拉树列表",notes = "有数据权限控制")
    public AjaxResult treeselect(SysAreaParam areaParam) {
        List<SysArea> areas = areaService.selectAreaList(areaParam);
        return AjaxResult.success(areaService.buildAreaTreeSelect(areas));
    }

}
