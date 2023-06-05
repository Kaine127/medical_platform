package com.mjs.medical.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.Schedule;
import com.mjs.medical.service.ScheduleService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据医生id获取对应的日程表
     * @param id
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<Schedule> list(@RequestParam("id") Long id){
        System.out.println("这里是日程表: "+id);
        LambdaQueryWrapper<Schedule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Schedule::getDoctorId,id);
        lambdaQueryWrapper.orderByAsc(Schedule::getRegister);
        List<Schedule> scheduleList = scheduleService.list(lambdaQueryWrapper);

        return ResponseResult.okResult(scheduleList);
    }

    /**
     * 添加新的时间段日程 或者 更新对应的时间段日程
     * @param schedule
     * @return
     */
    @PostMapping("/add")
    public ResponseResult<String> addSchedule(@RequestBody Schedule schedule){
        log.info("这里是schedule",schedule);
        schedule.addEnsureOnly();
        if(schedule.getId() == null){
            scheduleService.save(schedule);
        }else{
            scheduleService.updateById(schedule);
        }
        return ResponseResult.okResult(CodeEnum.SUCCESS);
    }

    /**
     * 删除对应id的日程表时间段项
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseResult<String> deleteSchedule(@RequestParam("id") Long id){
        System.out.println("这里是删除方法"+id);
        scheduleService.removeById(id);
        return ResponseResult.okResult(CodeEnum.SUCCESS);
    }
}
