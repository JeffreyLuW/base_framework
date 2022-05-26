package com.meide.controller;

import com.meide.common.utils.SimpleObjectRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisTestController {

    final SimpleObjectRedisUtil objectRedisUtil;

    @RequestMapping("/redistest")
    public Object test1(){
        objectRedisUtil.set("key~","value~");
        return objectRedisUtil.get("key~");
    }


}
