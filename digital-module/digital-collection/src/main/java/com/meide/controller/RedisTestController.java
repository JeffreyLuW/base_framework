package com.meide.controller;

import com.meide.common.FinalVariable;
import com.meide.model.PO.CollectionData;
import com.meide.service.collectionService.PopDistributeService;
import com.meide.utils.SimpleObjectRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class RedisTestController {

    final SimpleObjectRedisUtil objectRedisUtil;

    final PopDistributeService popDistributeService;



    @RequestMapping("/redis")
    public Object test1(){
        for(int i=0; i<FinalVariable.COLLECTION_DATA_LENGTH; i++){
            objectRedisUtil.lSet(FinalVariable.COLLECTION_DATA_KEY,CollectionData.builder()
                    .collectTs(LocalDateTime.now().toString())
                    .sensorData(i+".00")
                    .equipmentId("equipment001")
                    .sensorId(i+"").build()
            );
        }
        return "success";
    }
    @RequestMapping("/job")
    public Object job() throws InterruptedException {
        boolean execute = popDistributeService.execute();
        return execute;
    }

    @GetMapping("/lset/{v}")
    public Object lset(@PathVariable int v) throws InterruptedException {
        boolean b = objectRedisUtil.lSet("test", v);
        return objectRedisUtil.lGet("test",0,100000);
    }

    @GetMapping("/lget/{start}/{end}")
    public Object lget(@PathVariable int start, @PathVariable int end) throws InterruptedException {
        return objectRedisUtil.lGet("test",start,end);
    }

    @GetMapping("/ltrim/{start}/{end}")
    public Object ltrim(@PathVariable int start, @PathVariable int end) throws InterruptedException {
        objectRedisUtil.lTrim("test",start,end);
        return objectRedisUtil.lGet("test",0,1000000);
    }



}
