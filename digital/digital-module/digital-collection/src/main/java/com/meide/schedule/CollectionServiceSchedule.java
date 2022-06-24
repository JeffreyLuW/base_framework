package com.meide.schedule;

import com.meide.common.CommonVariable;
import com.meide.common.FinalVariable;
import com.meide.service.collectionService.PopDistributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class CollectionServiceSchedule {

    final PopDistributeService distributeService;

    // @Scheduled(fixedDelay = 1000 * 5 )
    void startJob(){
        // 采集服务
        if (CommonVariable.COLLECTION_MODE == 1)
            startDisribute();
        // 分发服务
        if (CommonVariable.DISTRIBUTE_MODE == 1)
            startDisribute();
        // 历史记录服务
        if (CommonVariable.HISTORY_MODE == 1)
            startDisribute();
        // 告警服务
        if (CommonVariable.ALERT_MODE == 1)
            startDisribute();
    }

    void startCollect(){

    }
    void startDisribute(){
        for (int i=0; i<FinalVariable.DISTRIBUTE_THREAD_NUM; i++){
            distributeService.start();
        }
    }
    void startHistory(){

    }
    void startAlert(){

    }

}
