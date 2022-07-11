package com.meide.demo.controller;

import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.demo.service.GroupDeviceBindService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/GroupDevice")
public class GroupDeviceBindController extends BaseController {

    final GroupDeviceBindService service;


    /**
     * 更改device归属
     * */
    @PutMapping
    public AjaxResult updateBind(@RequestParam("groupId") int groupId,
                                 @RequestParam("deviceType") String deviceType,
                                 @RequestParam("deviceList") List<Integer> deviceList){

        return rows("");
    }


}
