package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.entity.Schedule;
import com.mjs.medical.entity.ScheduleOption;
import com.mjs.medical.mapper.ScheduleOptionMapper;
import com.mjs.medical.service.ScheduleOptionService;
import com.mjs.medical.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleOptionServiceImpl extends ServiceImpl<ScheduleOptionMapper, ScheduleOption> implements ScheduleOptionService {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 获取修改时剩余可添加的出诊时间列表
     * @param doctorId
     * @return
     */
    @Transactional
    @Override
    public List<ScheduleOption> getScheduleOptions(Long doctorId,Long register){
        //获取该医生id下的出诊时间

        LambdaQueryWrapper<Schedule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Schedule::getDoctorId,doctorId);
        lambdaQueryWrapper.orderByAsc(Schedule::getRegister);
        List<Schedule> scheduleList = scheduleService.list(lambdaQueryWrapper);
        //拿出对应的map集合
        Integer needRemove = register.intValue();
        List<Integer> originList = scheduleList.stream().map(schedule -> schedule.getRegister()).collect(Collectors.toList());
        System.out.println("这里是原始数组: "+originList);
        Iterator<Integer> it = originList.iterator();
        while(it.hasNext()) {
            if(needRemove == it.next()) {
                it.remove();
            }
        }
        System.out.println("这里是处理后数组: "+originList);

        LambdaQueryWrapper<ScheduleOption> optionQueryWrapper = new LambdaQueryWrapper<>();
        if (originList.size() != 0){
            optionQueryWrapper.notIn(ScheduleOption::getValue,originList);
        }
        optionQueryWrapper.orderByAsc(ScheduleOption::getValue);
        List<ScheduleOption> scheduleOptions = this.list(optionQueryWrapper);
        return  scheduleOptions;
    }

    /**
     * 获取剩余新增时可添加的出诊时间列表
     * @param doctorId
     * @return
     */
    @Override
    public List<ScheduleOption> getAddScheduleOptions(Long doctorId) {
        //获取该医生id下的出诊时间

        LambdaQueryWrapper<Schedule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Schedule::getDoctorId,doctorId);
        lambdaQueryWrapper.orderByAsc(Schedule::getRegister);
        List<Schedule> scheduleList = scheduleService.list(lambdaQueryWrapper);
        System.out.println("这里是新增的");
        if (scheduleList.size() == 0){
            // 没有需要剔除的 直接返回完整列表
            return list(Wrappers.<ScheduleOption>lambdaQuery().orderByAsc(ScheduleOption::getValue));
        }else {
            // 需要剔除某些选项
            LambdaQueryWrapper<ScheduleOption> optionQueryWrapper = new LambdaQueryWrapper<>();
            List<Integer> originList = scheduleList.stream().map(schedule -> schedule.getRegister()).collect(Collectors.toList());
            optionQueryWrapper.notIn(ScheduleOption::getValue,originList);
            optionQueryWrapper.orderByAsc(ScheduleOption::getValue);
            List<ScheduleOption> scheduleOptions = this.list(optionQueryWrapper);
            return  scheduleOptions;
        }

    }
}
