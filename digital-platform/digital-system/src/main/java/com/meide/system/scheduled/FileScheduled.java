package com.meide.system.scheduled;

import com.meide.system.service.ISysFilelinkService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jiay
 */
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
//@EnableScheduling   // 2.开启定时任务
public class FileScheduled {

    @Resource
    private ISysFilelinkService iSysFilelinkService;

    //@Scheduled(cron = "0 0 3 * * ?")
    //@Scheduled(cron = "0/15 * * * * ?")
    private void dealFile() {
        /**
         * 每天定时删除sys_file中不需要的文件
         * 根据sign_count<=0 去删除
         * sign_count 是调用文件上传时
         */
        iSysFilelinkService.dealFile();
    }

}
