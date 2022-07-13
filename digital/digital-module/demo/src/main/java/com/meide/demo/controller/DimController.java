package com.meide.demo.controller;

import com.meide.demo.service.DimService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiay
 * @date 2022/6/15
 */
@Api(tags = "Dim测试")
@RestController
@RequestMapping("/dim")
public class DimController {
    @Autowired
    DimService dimService;

    @ApiOperation("获取dim列表")
    @GetMapping("/demo")
    public void demo(){
        dimService.query();
        dimService.querySlave();
    }
}
