package com.meide.service.collectionService;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.meide.common.CommonVariable;
import com.meide.common.FinalVariable;
import com.meide.model.PO.CollectionData;
import com.meide.utils.DozerUtil;
import com.meide.utils.SimpleObjectRedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 二、数据分发服务
 * 将采集数据复制、组装成其他模块所需数据并存放进redis
 * */

@Slf4j
@Component
@RequiredArgsConstructor
public class PopDistributeService {

    final SimpleObjectRedisUtil objectRedisUtil;

    @Async
    public void start(){
        try {
            while(true){
                // 执行数据分发
                boolean executeRes = execute();
                // 如果没有数据这个线程就歇会
                if(!executeRes)
                    Thread.sleep(1000 * 10);

                log.info("------======= 采集数据数据不足 ======------");

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean execute() throws InterruptedException {
        log.info("------======= 开始分发 ======------");
        // 拿取redis采集数据
        // lget按下标获取
        List<Object> objectList = objectRedisUtil.lGet(FinalVariable.COLLECTION_DATA_KEY, 0, FinalVariable.COLLECTION_DATA_LENGTH - 1);
        // 数据不足
        if(CollectionUtils.isEmpty(objectList) || objectList.size() < FinalVariable.COLLECTION_DATA_LENGTH)
            return false;

        // 给历史数据模块添加数据
        objectRedisUtil.lSet(FinalVariable.HISTORY_DATA_KEY, objectList);

        // 只有数据发生改变才去替换redis和COMM_COL_MAP中数据
        ConcurrentHashMap<String, CollectionData> commMap = CommonVariable.COMM_COL_MAP;
        DozerUtil.mapList(objectList, CollectionData.class).forEach(d1 -> {
            // 数据一致则跳过
            if(d1.equals(commMap.get(d1.getSensorId()))) return;

            // 更改redis中实时数据
            objectRedisUtil.hset(d1.getSensorId(), "sensorData", d1.getSensorData());
            // 更新缓存数据
            commMap.put(d1.getSensorId(), d1);

        });
        // 清理redis中使用过的1000条采集数据
        // ltrim 截取并保留下标部分，删除下标之外的部分
        objectRedisUtil.lTrim(FinalVariable.COLLECTION_DATA_KEY, FinalVariable.COLLECTION_DATA_LENGTH, 999999999999L);
        Thread.sleep(10000);
        return true;
    }

}
