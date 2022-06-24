package com.meide;

import com.meide.common.CommonVariable;
import com.meide.utils.CheckValue;
import com.meide.utils.EncryptorUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@MapperScan("com.meide.mapper")
public class MainApplication {

    public static void main(String[] args) {

        CommonVariable.COLLECTION_MODE = CheckValue.getIntegerByObject(args[0]);
        CommonVariable.DISTRIBUTE_MODE = CheckValue.getIntegerByObject(args[1]);
        CommonVariable.HISTORY_MODE = CheckValue.getIntegerByObject(args[2]);
        CommonVariable.ALERT_MODE = CheckValue.getIntegerByObject(args[3]);

        SpringApplication.run(MainApplication.class, args);
    }

}
