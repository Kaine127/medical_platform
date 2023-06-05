package com.mjs.medical.controller;

import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.ScheduleOption;
import com.mjs.medical.service.ScheduleOptionService;
import com.mjs.medical.service.impl.ScheduleOptionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/scheduleOption")
public class ScheduleOptionController {

    @Autowired
    private ScheduleOptionService scheduleOptionService;

    /**
     * 获取可添加的出诊时间列表
     * @param doctorId
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<List<ScheduleOption>> list(@RequestParam("id") Long doctorId,@RequestParam("register") Long register){
        List<ScheduleOption> scheduleOptions = scheduleOptionService.getScheduleOptions(doctorId,register);
        System.out.println(scheduleOptions);
        return ResponseResult.okResult(scheduleOptions);
    }

    /**
     * 获取新增时可添加的出诊时间列表
     * @param doctorId
     * @return
     */
    @GetMapping("/add")
    public ResponseResult<List<ScheduleOption>> add(@RequestParam("id") Long doctorId){
        System.out.println("获取新增时可添加的出诊时间列表");
        List<ScheduleOption> scheduleOptions = scheduleOptionService.getAddScheduleOptions(doctorId);
        System.out.println(scheduleOptions);
        return ResponseResult.okResult(scheduleOptions);
    }
}
