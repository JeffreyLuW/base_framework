package com.meide.controller;

import com.meide.common.DataSourceContextHolder;
import com.meide.service.TaosTestService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaosTestController {

    final TaosTestService taosTestService;

    @RequestMapping("/taostest")
    public Object taostest(){
        return taosTestService.test();
    }


}
