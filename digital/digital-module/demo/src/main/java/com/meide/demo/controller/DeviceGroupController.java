package com.meide.demo.controller;

import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.PageResult;
import com.meide.demo.domain.PO.DeviceGroup;
import com.meide.demo.service.DeviceGroupService;
import com.meide.demo.utils.ObjectFieldUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class DeviceGroupController extends BaseController {

    final DeviceGroupService service;

    /**
     * 增加device组（根/子）
     */
    @PostMapping
    public AjaxResult insert(@RequestBody DeviceGroup group){

        if(ObjectUtils.isEmpty(group) ||
                ObjectFieldUtils.isExistSpecifiedFieldIsEmpty(group,"groupName", "groupType"))
            return AjaxResult.errorWithMsg();

        return rows(service.createGroup(group));
    }

    /**
     * 删除device组
     * */
    @DeleteMapping("/{id}")
    public AjaxResult dropGroupById(@PathVariable int id){
        return rows(service.dropGroupAndDevice(id));
    }

    /**
     * 编辑组名
     * */
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceGroup group){

        if(ObjectUtils.isEmpty(group) ||
                ObjectFieldUtils.isExistSpecifiedFieldIsEmpty(group,"groupId", "groupName"))
            return AjaxResult.errorWithMsg();

        return rows(service.editById(DeviceGroup.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .build()));
    }

    /**
     * 查询device组列表
     * */
    @GetMapping("/{type}")
    public PageResult<DeviceGroup> queryList(@PathVariable String type){
        startPage();
        return page(service.queryList(type));
    }

    /**
     * 查询device组树形列表
     * */
    @GetMapping("/tree/{type}")
    public AjaxResult queryTreeList(@PathVariable String type){
        return rows(service.queryTreeList(type));
    }

    /**
     * 编辑树形列表/更改树形图顺序
     * */
    @PutMapping("/sort")
    public AjaxResult updateTree(){
        // todo
        return null;
    }


}
