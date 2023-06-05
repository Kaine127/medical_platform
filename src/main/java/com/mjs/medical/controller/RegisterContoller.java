package com.mjs.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mjs.medical.common.BaseContext;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.*;
import com.mjs.medical.entity.dto.*;
import com.mjs.medical.service.EmployeeService;
import com.mjs.medical.service.RegisterService;
import com.mjs.medical.service.ScheduleOnlineService;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterContoller {

    @Autowired
    private RegisterService registerService;

    /**
     * 就诊者用户预约挂号
     * @return
     */
    @PostMapping("/doRegister")
    public ResponseResult doRegister(@RequestBody ScheduleOnlineDto dto){
        System.out.println("这里是进行预约挂号的地方"+dto);
        return registerService.doRegister(dto);                                                                                            //
    }

    /**
     * 根据医生id获取可以进行预约的挂号信息
     * @param dto
     * @return
     */
    @PostMapping("getRegisterList")
    public ResponseResult getRegisterList(@RequestBody DoctorDto dto){

        return registerService.getRegisterList(dto);
    }

    /**
     * 用户获取
     * @return
     */
    @GetMapping("/getRegisterDoctor")
    public ResponseResult getRegisterDoctor(){
//        log.info("到达获取挂号医生列表方法里面了");
//        List<ScheduleOnline> scheduleOnlineList = scheduleOnlineService
//                .list(Wrappers.<ScheduleOnline>lambdaQuery().eq(ScheduleOnline::getStatus,1));
//        //获取可以挂号的用户id
//        Set<Long> doctorIds = scheduleOnlineList.stream().map((scheduleOnline -> scheduleOnline.getDoctorId())).collect(Collectors.toSet());
//        //获取医生信息
//        List<Employee> doctors = employeeService.listByIds(doctorIds);
//        //从集合中获取医生信息
//        Map<Long,String> doctorMap = doctors.stream().collect(Collectors
//                .toMap(Employee::getId,Employee::getName));
//        System.out.println(doctorMap);

        return registerService.getRegisterDoctor();
    }

    /**
     * 根据用户id获取已经挂号的信息
     * @return
     */
    @GetMapping("/getUserRegisterList")
    public ResponseResult getUserRegisterList(){
        Long userId = BaseContext.getCurrentId();
        List<Register> registerList = registerService.list(Wrappers.<Register>lambdaQuery()
                .eq(Register::getUserId, userId)
                .eq(Register::getStatus, 0)
                .orderByAsc(Register::getCreateTime));
        return ResponseResult.okResult(registerList);

    }

    @Autowired
    private ScheduleOnlineService scheduleOnlineService;
    /**
     * 根据挂号id删除挂号信息
     * @param dto
     * @return
     */
    @DeleteMapping("/registerDelete")
    public ResponseResult registerDelete(@RequestBody RegisterDeleteDto dto){
        System.out.println("这里是挂号删除方法");
        Register register = registerService.getById(dto.getId());
        //恢复可挂号数
        LambdaQueryWrapper<ScheduleOnline> lambdaQueryWrapper = new LambdaQueryWrapper<ScheduleOnline>();
        lambdaQueryWrapper.eq(ScheduleOnline::getRegister,register.getRegisterNumber());
        lambdaQueryWrapper.eq(ScheduleOnline::getDoctorId,register.getDoctorId());
        lambdaQueryWrapper.eq(ScheduleOnline::getStatus,1);
        ScheduleOnline scheduleOnline = scheduleOnlineService.getOne(lambdaQueryWrapper);
        scheduleOnline.setNumber(scheduleOnline.getNumber() + 1);
        scheduleOnline.setNumberMax(scheduleOnline.getNumberMax() + 1);
        scheduleOnlineService.updateById(scheduleOnline);
        boolean check = registerService.removeById(dto.getId());
        if (check){
            return ResponseResult.okResult("删除成功");
        }
        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }

    /**
     * 诊所前台实时挂号所需要的医生名单
     * @return
     */
    @GetMapping("/getDoctorName")
    public ResponseResult getDoctorName(){
        return registerService.getDoctorName();
    }

    @PostMapping("/registerNow")
    public ResponseResult registerNow(@RequestBody RegisterPatientDto dto){
        System.out.println("这里是进行实时挂号的参数检验"+dto);
        return registerService.registerNow(dto);
    }

    @PutMapping("/doSign")
    @Transactional
    public ResponseResult doSign(@RequestBody SignDto dto){
        System.out.println("这里是签到操作");
        Register register = registerService.getOne(Wrappers.<Register>lambdaQuery()
                .eq(Register::getSign, dto.getSign())
                .eq(Register::getStatus,0));
        if (register == null){
            return ResponseResult.errorResult(CodeEnum.PARAM_INVALID);
        }
        register.setStatus(1);
        boolean result = registerService.updateById(register);
        if (result){
            return ResponseResult.okResult("签到成功");
        }
        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }

    /**
     * 获取实时叫号列表
     * @return
     */
    @GetMapping("/getRealTimeRegister")
    public ResponseResult getRealTimeRegister(){
        System.out.println("这里是实时叫号队列");
        //获得当前的时间段
        LocalDateTime localDateTime = LocalDateTime.now();
        int num = (localDateTime.getDayOfWeek().getValue()-1)*2 + (localDateTime.get(ChronoField.AMPM_OF_DAY)+1);
        List<Register> registerList = registerService.list(Wrappers.<Register>lambdaQuery().eq(Register::getRegisterNumber, num)
                .eq(Register::getStatus, 1)
                .eq(Register::getDoctorId,BaseContext.getCurrentId()));
        return ResponseResult.okResult(registerList);
    }

    /**
     * 实时叫号项转候补
     * @param register
     * @return
     */
    @PostMapping("/toCandidate")
    @Transactional
    public ResponseResult toCandidate(@RequestBody Register register){
        register.setStatus(2);
        boolean result = registerService.updateById(register);
        if (result){
            return ResponseResult.okResult("修改成功");
        }
        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }

    /**
     * 获取候补叫号队列
     * @return
     */
    @GetMapping("/getCandidateRegister")
    public ResponseResult getCandidateRegister(){
        System.out.println("这里是候补叫号队列");
        //获得当前的时间段
        LocalDateTime localDateTime = LocalDateTime.now();
        int num = (localDateTime.getDayOfWeek().getValue()-1)*2 + (localDateTime.get(ChronoField.AMPM_OF_DAY)+1);
        List<Register> registerList = registerService.list(Wrappers.<Register>lambdaQuery().eq(Register::getRegisterNumber, num)
                .eq(Register::getStatus, 2)
                .eq(Register::getDoctorId,BaseContext.getCurrentId()));
        return ResponseResult.okResult(registerList);
    }

    @GetMapping("/getRegisterCount")
    public ResponseResult getRegisterCount(){
        LocalDateTime localDateTime = LocalDateTime.now();
        int value = localDateTime.getDayOfWeek().getValue()*2;
        int notSign = registerService.count(Wrappers.<Register>lambdaQuery()
                .eq(Register::getStatus, 0)
                .in(Register::getRegisterNumber, value - 1, value));
        int sign = registerService.count(Wrappers.<Register>lambdaQuery()
                .eq(Register::getStatus, 1)
                .in(Register::getRegisterNumber, value - 1, value));
        int wait = registerService.count(Wrappers.<Register>lambdaQuery()
                .eq(Register::getStatus, 2)
                .in(Register::getRegisterNumber, value - 1, value));
        List<PieData> dataList = new ArrayList<>();
        dataList.add(new PieData(notSign,"未签到"));
        dataList.add(new PieData(sign,"已签到"));
        dataList.add(new PieData(wait,"候补"));

        return ResponseResult.okResult(dataList);

    }

    /**
     * 完成问诊
     * @param dto
     * @return
     */
    @PutMapping("/completeInquiry")
    public ResponseResult completeInquiry(@RequestBody RegisterDto dto){
        LambdaUpdateWrapper<Register> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.eq(Register::getId,dto.getId());
        lambdaUpdateWrapper.set(Register::getStatus,3);
        registerService.update(lambdaUpdateWrapper);
        return ResponseResult.okResult("完成更新");
    }

    /**
     * 取消挂号
     * @param dto
     * @return
     */
    @PutMapping("/cancelRegister")
    public ResponseResult cancelRegister(@RequestBody RegisterDto dto){
        LambdaUpdateWrapper<Register> lambdaUpdateWrapper  = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Register::getId,dto.getId());
        lambdaUpdateWrapper.set(Register::getStatus,4);
        registerService.update(lambdaUpdateWrapper);
        return ResponseResult.okResult("完成取消操作");
    }

}
