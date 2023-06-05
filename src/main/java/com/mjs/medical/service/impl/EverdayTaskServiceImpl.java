package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mjs.medical.entity.Register;
import com.mjs.medical.entity.Schedule;
import com.mjs.medical.entity.ScheduleOnline;
import com.mjs.medical.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EverdayTaskServiceImpl implements EverydayTaskService {
    @Autowired
    private ScheduleOnlineService scheduleOnlineService;

    @Autowired
    private ScheduleService scheduleService;
    @Override
    public void doTaskAboutSchedule() {
        //进行启用预约时间段表的创建和废弃
        // 获取当前天数
        LocalDateTime localDateTime = LocalDateTime.now();
        int today = localDateTime.getDayOfWeek().getValue()*2;
        // 每天遍历schedule表中三天后两个时间段的启用可预约时间项
        // 周二上午 可以预约  4 5 6 7 8 9 下午可以月 5 6 7 8 9 10 所以周二凌晨验证与创建 4 5 6 7 8 9 10
        for (int i = 0; i < 7; i++){
            if (today + i <= 14){
                createScheduleOnline(today + i,i);
            }else {
                createScheduleOnline((today + i) - 14,i);
            }
        }
        // 废弃之前一天中两个时间段的启用可预约时间项
        disableScheduleOnline();
    }

    public void createScheduleOnline(int registerNumber,int addCount){
        System.out.println("这里是创建方法");
        //根据传递过来时间段数值获取所有用的预约时间段列表
        List<Schedule> scheduleList = scheduleService.list(Wrappers.<Schedule>lambdaQuery()
                .eq(Schedule::getRegister, registerNumber)
                .eq(Schedule::getStatus, 1));
        //遍历列表,一次检查是否进行了创建
        for (Schedule schedule : scheduleList) {
            ScheduleOnline online = scheduleOnlineService.getOne(Wrappers.<ScheduleOnline>lambdaQuery()
                    .eq(ScheduleOnline::getScheduleId, schedule.getId())
                    .eq(ScheduleOnline::getStatus, 1));
            //判断是否在存在
            if (online == null ){
                //为空, 进行创建
                System.out.println("本次预约时间为空,挂号表id为:"+schedule.getId());
                ScheduleOnline scheduleOnline = new ScheduleOnline();
                scheduleOnline.setDoctorId(schedule.getDoctorId());
                scheduleOnline.setScheduleId(schedule.getId());
                scheduleOnline.setName(schedule.getName());
                scheduleOnline.setRegister(schedule.getRegister());
                scheduleOnline.setNumber(schedule.getNumber());
                scheduleOnline.setNumberMax(schedule.getNumberMax());
                scheduleOnline.setWeek(getWeek(schedule.getRegister()));
                scheduleOnline.setRegisterDay(getRegisterDay(addCount + 1));
                scheduleOnline.setCreateTime(LocalDateTime.now());
                scheduleOnline.setStatus(1);
                scheduleOnlineService.save(scheduleOnline);
            }else {
                //存在 不需要创建
                log.info("该时间段已经创建",schedule.getRegister());
            }
        }


    }

    @Autowired
    private RegisterService registerService;
    /**
     * 废弃过期的
     */
    @Override
    public void disableScheduleOnline() {
        System.out.println("这里是废弃方法");
        LocalDateTime localDateTime = LocalDateTime.now();
        // 周二上午为3   2*2 -1  不需要废弃的为 上午和要创建的预约时间段 即 3 4 5 6 7 8 9 10
        int num = localDateTime.getDayOfWeek().getValue()*2 - 1;
        List<Integer> registerNums = new ArrayList<Integer>();

        for(int i = 0; i < 8 ; i++){
            if( (num + i) >14){
                registerNums.add((num + i) - 14);
            }else {
                registerNums.add(num+i);
            }
        }
        LambdaUpdateWrapper<ScheduleOnline> lambdaUpdateWrapper = new LambdaUpdateWrapper<ScheduleOnline>();
        lambdaUpdateWrapper.set(ScheduleOnline::getStatus,0);
        lambdaUpdateWrapper.notIn(ScheduleOnline::getRegister,registerNums);
        scheduleOnlineService.update(lambdaUpdateWrapper);
        LambdaUpdateWrapper<Register> lambdaUpdateWrapper2 = new LambdaUpdateWrapper<Register>();
        lambdaUpdateWrapper2.set(Register::getStatus,4);
        lambdaUpdateWrapper2.notIn(Register::getRegisterNumber,registerNums);
        lambdaUpdateWrapper2.notIn(Register::getStatus,3);
        registerService.update(lambdaUpdateWrapper2);
    }

    //返回周几上下午
    public String getWeek(int registerNumber){
        String week = "";
        switch (registerNumber){
            case 1 :
                week = "周一上午";
                break;
            case 2 :
                week = "周一下午";
                break;
            case 3 :
                week = "周二上午";
                break;
            case 4 :
                week = "周二下午";
                break;
            case 5 :
                week = "周三上午";
                break;
            case 6 :
                week = "周三下午";
                break;
            case 7 :
                week = "周四上午";
                break;
            case 8 :
                week = "周四下午";
                break;
            case 9 :
                week = "周五上午";
                break;
            case 10 :
                week = "周五下午";
                break;
            case 11 :
                week = "周六上午";
                break;
            case 12 :
                week = "周六下午";
                break;
            case 13 :
                week = "周日上午";
                break;
            case 14 :
                week = "周日下午";
                break;
            default:
                week = "未知日期";
                break;
        }
        System.out.println("这里是getWeek,本次返回的是:"+week);
        return week;
    }

    //返回预约的日期
    public String getRegisterDay(int addCount){
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime plusHours = localDateTime.plusHours(addCount * 12);
        int monthValue = plusHours.getMonthValue();
        int dayOfMonth = plusHours.getDayOfMonth();
        String registerDay = monthValue + "月" + dayOfMonth + "日";
        System.out.println("这里是getRegisterDay,本次返回的是"+registerDay);
        return registerDay;
    }


}
