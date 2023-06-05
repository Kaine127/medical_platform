package com.mjs.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mjs.medical.common.BaseContext;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.Employee;
import com.mjs.medical.entity.Patient;
import com.mjs.medical.entity.dto.IdNumberDto;
import com.mjs.medical.entity.dto.PatientDto;
import com.mjs.medical.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 用户获取账号下的就诊者列表
     * @param id
     * @return
     */
    @Cacheable(value = "userPatientList",key = "#id", unless = "#result == null")
    @GetMapping("/getPatientList")
    public ResponseResult getPatientList(@RequestParam("userId")Long id){
        System.out.println("进入到获取就诊者列表了"+id);
        List<Patient> patientList = patientService.list(Wrappers.<Patient>lambdaQuery()
                .eq(Patient::getUserId, id).orderByAsc(Patient::getCreateTime));
        return ResponseResult.okResult(patientList);
    }

    /**
     * 在挂号时获取对应就诊者用户下的就诊者信息列表
     * @return
     */
    @GetMapping("/getPatientSelections")
    public ResponseResult getRegisterList(){
//        LocalDateTime localDateTime = LocalDateTime.now();
//        System.out.println(localDateTime);
//        System.out.println(localDateTime.getDayOfWeek().getValue());
//        System.out.println(localDateTime.get(ChronoField.AMPM_OF_DAY));
//        System.out.println(localDateTime.getMonth().getValue());
//        System.out.println(localDateTime.getDayOfMonth());
//        //组成当前的时间
//        String registerDay = localDateTime.getMonthValue()+"月"+localDateTime.getDayOfMonth()+"日";
//        System.out.println(registerDay);
//        //计算预约挂号值
//        int num = (localDateTime.getDayOfWeek().getValue()-1)*2 + (localDateTime.get(ChronoField.AMPM_OF_DAY)+1);
//        System.out.println(num);
        Long userId = BaseContext.getCurrentId();
        List<Patient> patientList = patientService.list(Wrappers.<Patient>lambdaQuery()
                .eq(Patient::getUserId, userId).orderByAsc(Patient::getCreateTime));
        // 通过Stream流获取就诊者姓名列表, 挂号时用的id 用就诊者用户和就诊者姓名共同查询
        List<String> patientNameList = patientList.stream().map(patient -> patient.getPatientName()).collect(Collectors.toList());
        return ResponseResult.okResult(patientNameList);
    }

    /**
     * 诊所前台获取就诊者信息列表
     * @return
     */
    @GetMapping("/getList")
    public ResponseResult<Page> list(int page,int pageSize,String name){
        //分页构造器
        Page<Patient> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Patient> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Patient::getPatientName,name);
        lambdaQueryWrapper.orderByAsc(Patient::getCreateTime);
        patientService.page(pageInfo,lambdaQueryWrapper);

        return ResponseResult.okResult(pageInfo);
    }

    /**
     * 诊所前台更新就诊者信息
     * @param patient
     * @return
     */
    @PutMapping("/updatePatient")
    public ResponseResult updatePatient(@RequestBody Patient patient){
        System.out.println("这里是更新就诊者"+patient);
        boolean result = patientService.updateById(patient);
        if (result){
            return ResponseResult.okResult("就诊者更新成功");
        }

        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }

    /**
     * 诊所前台删除就诊者 逻辑删除
     * @param patient
     * @return
     */
    @DeleteMapping("/receptionDelete")
    public ResponseResult receptionDelete(@RequestBody Patient patient){
        System.out.println("这里是删除方法"+patient);
        boolean result = patientService.removeById(patient.getId());
        if (result){
            return ResponseResult.okResult("删除成功");
        }
        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }

    /**
     * 诊所前台员工添加就诊者信息
     * @param patient
     * @return
     */
    @PostMapping("/receptionAddPatient")
    public ResponseResult receptionAddPatient(@RequestBody Patient patient){
        LocalDateTime localDateTime = LocalDateTime.now();
        patient.setUpdateTime(localDateTime);
        patient.setCreateTime(localDateTime);
        boolean save = patientService.save(patient);
        if (save){
            return ResponseResult.okResult("添加成功");
        }
        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }

    /**
     * 检查身份证号 先查看是否存在此身份证号的就诊者 如果有 检查是否被绑定
     */
    @PostMapping("/checkIdNumber")
    public ResponseResult checkIdNumber(@RequestBody IdNumberDto dto){
        System.out.println("这里是要被检查的id"+dto);
        Patient patient = patientService.getOne(Wrappers.<Patient>lambdaQuery()
                .eq(Patient::getIdNumber, dto.getIdNumber()));
        if (patient.getUserId() != null){
            System.out.println("身份证存在");
            return ResponseResult.errorResult(CodeEnum.DATA_EXIST);
        }
        return ResponseResult.okResult("可以使用该用户");

    }

    /**
     * 诊所医生获取就诊者的信息
     * @param dto
     * @return
     */
    @PostMapping("/getPatientInfo")
    public ResponseResult getPatientInfo(@RequestBody PatientDto dto){
        Patient patient = patientService.getById(dto.getPatientId());
        return ResponseResult.okResult(patient);
    }



}
