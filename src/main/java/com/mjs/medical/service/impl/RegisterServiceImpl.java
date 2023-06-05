package com.mjs.medical.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.common.BaseContext;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.Employee;
import com.mjs.medical.entity.Patient;
import com.mjs.medical.entity.Register;
import com.mjs.medical.entity.ScheduleOnline;
import com.mjs.medical.entity.dto.DoctorDto;
import com.mjs.medical.entity.dto.RegisterPatientDto;
import com.mjs.medical.entity.dto.ScheduleOnlineDto;
import com.mjs.medical.mapper.RegisterMapper;
import com.mjs.medical.service.EmployeeService;
import com.mjs.medical.service.PatientService;
import com.mjs.medical.service.RegisterService;
import com.mjs.medical.service.ScheduleOnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegisterServiceImpl extends ServiceImpl<RegisterMapper, Register> implements RegisterService {

    @Autowired
    private ScheduleOnlineService scheduleOnlineService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PatientService patientService;

    /**
     * 获取可以预约的医生列表
     * @return
     */
    @Override
    @Transactional
    public ResponseResult getRegisterDoctor() {
        log.info("到达获取挂号医生列表方法里面了");
        List<ScheduleOnline> scheduleOnlineList = scheduleOnlineService
                .list(Wrappers.<ScheduleOnline>lambdaQuery().eq(ScheduleOnline::getStatus,1));
        //获取可以挂号的用户id
        Set<Long> doctorIds = scheduleOnlineList.stream().map((scheduleOnline -> scheduleOnline.getDoctorId())).collect(Collectors.toSet());
        //获取医生信息
        List<Employee> doctors = employeeService.listByIds(doctorIds);
        //从集合中获取医生信息
        Map<Long,String> doctorMap = doctors.stream().collect(Collectors
                .toMap(Employee::getId,Employee::getName));
        System.out.println(doctorMap);

        return ResponseResult.okResult(doctorMap);
    }

    /**
     * 根据医生id进行预约列表的操作
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult getRegisterList(DoctorDto dto) {
        System.out.println("这里是预约列表"+dto);
        LocalDateTime localDateTime = LocalDateTime.now();
        //获取可预约的时间值
        int num = (localDateTime.getDayOfWeek().getValue()-1)*2 + (localDateTime.get(ChronoField.AMPM_OF_DAY)+2);
        List<Integer> registerNums = new ArrayList<Integer>();

        for(int i = 0; i < 6 ; i++){
            if( (num + i) >14){
                registerNums.add((num + i) - 14);
            }else {
                registerNums.add(num+i);
            }
        }
        System.out.println("这里是获取列表"+registerNums);
        // 状态为启用的 线上预约人数大于0 对应医生id 在预约时间段中
        LambdaQueryWrapper<ScheduleOnline> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ScheduleOnline::getStatus,1);
        lambdaQueryWrapper.gt(ScheduleOnline::getNumber,0);
        lambdaQueryWrapper.eq(ScheduleOnline::getDoctorId,dto.getDoctorId());
        lambdaQueryWrapper.in(ScheduleOnline::getRegister,registerNums);
        lambdaQueryWrapper.orderByAsc(ScheduleOnline::getRegister);
        List<ScheduleOnline> scheduleOnlineList = scheduleOnlineService.list(lambdaQueryWrapper);
        System.out.println(scheduleOnlineList);
        return ResponseResult.okResult(scheduleOnlineList);
    }

    /**
     * 用户预约操作
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult doRegister(ScheduleOnlineDto dto) {
        Register register = new Register();
        //填充信息
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //检查是否重复 不能有统一时间段中 统一姓名的就诊者
        Register checkOne = getOne(Wrappers.<Register>lambdaQuery().eq(Register::getUserId, userId)
                .eq(Register::getRegisterName, dto.getPatientName())
                .eq(Register::getRegisterNumber,dto.getRegister()).eq(Register::getStatus,0));
        if (checkOne != null){
            System.out.println("这里是因为有相同的挂号了");
            return ResponseResult.errorResult(CodeEnum.DATA_EXIST);
        }
        //获取挂号的就诊者信息 dto中只有name  需要其他信息
        Patient patient = patientService.getOne(Wrappers.<Patient>lambdaQuery().eq(Patient::getPatientName, dto.getPatientName()));
        register.setUserId(userId);
        register.setRegisterId(patient.getId());
        register.setRegisterName(patient.getPatientName());
        register.setRegisterNumber(dto.getRegister());
        register.setRegisterTime(LocalDateTime.now());
        register.setDoctorId(dto.getDoctorId());
        register.setDoctorName(dto.getName());
        register.setStatus(0);
        register.setCreateTime(LocalDateTime.now());
        register.setSign(Integer.valueOf(RandomUtil.randomNumbers(5)));
        register.setRegisterDay(dto.getRegisterDay());
        register.setWeek(dto.getWeek());
        System.out.println("这里是创建好的就诊者信息"+register);
        boolean save = save(register);
        if (save){
            //数量减一
            ScheduleOnline scheduleOnline = scheduleOnlineService.getById(dto.getId());
            scheduleOnline.setNumber(scheduleOnline.getNumber() - 1);
            scheduleOnline.setNumberMax(scheduleOnline.getNumberMax() - 1);
            scheduleOnlineService.updateById(scheduleOnline);
            return ResponseResult.okResult("挂号成功");
        }
        return ResponseResult.errorResult(CodeEnum.PARAM_INVALID);
    }

    /**
     * 诊所前台实时挂号所需要的医生名单
     * @return
     */
    @Override
    public ResponseResult getDoctorName() {
        LocalDateTime localDateTime = LocalDateTime.now();
        //得到当前的时间段数字 周四下午 -- 8   全部挂号人数大于0即可
        int num = (localDateTime.getDayOfWeek().getValue()-1)*2 + (localDateTime.get(ChronoField.AMPM_OF_DAY)+1);
        LambdaQueryWrapper<ScheduleOnline> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ScheduleOnline::getStatus,1);
        lambdaQueryWrapper.eq(ScheduleOnline::getRegister,num);
        lambdaQueryWrapper.gt(ScheduleOnline::getNumberMax,0);
        lambdaQueryWrapper.orderByAsc(ScheduleOnline::getRegister);
        List<ScheduleOnline> nowList = scheduleOnlineService.list(lambdaQueryWrapper);
        // 只需要医生的姓名
        Set<String> doctorNames = nowList.stream().map((scheduleOnline -> scheduleOnline.getName())).collect(Collectors.toSet());
        System.out.println("这里是获得实时挂号医生列表"+doctorNames);
        return ResponseResult.okResult(doctorNames);
    }

    /**
     * 诊所前台即时挂号
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult registerNow(RegisterPatientDto dto) {
        Register register = new Register();
        Employee doctor = employeeService.getOne(Wrappers.<Employee>lambdaQuery().eq(Employee::getName, dto.getDoctorName()));
        if (doctor == null){
            return ResponseResult.errorResult(CodeEnum.DATA_NOT_EXIST);
        }
        register.setRegisterId(dto.getId());
        register.setRegisterName(dto.getPatientName());
        LocalDateTime localDateTime = LocalDateTime.now();
        int num = (localDateTime.getDayOfWeek().getValue()-1)*2 + (localDateTime.get(ChronoField.AMPM_OF_DAY)+1);
        register.setRegisterNumber(num);
        register.setRegisterTime(localDateTime);
        register.setDoctorId(doctor.getId());
        register.setDoctorName(dto.getDoctorName());
        // 即时挂号不用签到
        register.setStatus(1);
        register.setCreateTime(localDateTime);
        int monthValue = localDateTime.getMonthValue();
        int dayOfMonth = localDateTime.getDayOfMonth();
        String registerDay = monthValue + "月" + dayOfMonth + "日";
        register.setRegisterDay(registerDay);
        boolean save = save(register);
        if (save) {
            return ResponseResult.okResult("挂号成功");
        }
        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }
}
