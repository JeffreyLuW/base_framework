package com.meide.demo.controller;

import com.meide.demo.service.DimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiay
 * @date 2022/6/15
 */
@RestController
@RequestMapping("/dim")
public class DimController {
    @Autowired
    DimService dimService;

    @GetMapping("/demo")
    public void demo(){
        dimService.query();
    }
}
