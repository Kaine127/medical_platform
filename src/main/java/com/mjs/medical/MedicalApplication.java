package com.mjs.medical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j //开启日志
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement //开启事务管理
@EnableCaching //开启缓存
@EnableScheduling//开启定时任务
public class MedicalApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicalApplication.class,args);
        log.info("项目启动成功");
    }
}
