package com.mjs.medical.controller;


import com.mjs.medical.service.EverydayTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/task")
public class EverdayTaskController {

    @Autowired
    private EverydayTaskService everydayTaskService;

    @Scheduled(cron ="0 1 0 * * ?") // 每天凌晨 0时1分进行任务
    @GetMapping("/doTask")
    public void doTaskAboutSchedule(){
        System.out.println("这里是doTask");
        everydayTaskService.doTaskAboutSchedule();
    }

    @GetMapping("/test")
    public void test(){
        LocalDateTime localDateTime = LocalDateTime.now();
    }
}
