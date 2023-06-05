package com.mjs.medical.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mjs.medical.common.BaseContext;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.Employee;
import com.mjs.medical.entity.Patient;
import com.mjs.medical.entity.User;
import com.mjs.medical.entity.dto.IdNumberDto;
import com.mjs.medical.entity.dto.UserLoginDto;
import com.mjs.medical.service.PatientService;
import com.mjs.medical.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据手机号 发送验证码
     * @param phone
     * @param session
     * @return
     */
    @GetMapping("/sms")
    public ResponseResult sendCode(@PathParam("phone") String phone, HttpSession session){
        System.out.println("这里是发送验证码");
        if (StringUtils.isBlank(phone)) {
            System.out.println("这里是手机号为空");
            return ResponseResult.errorResult(CodeEnum.PARAM_INVALID);
        }
        //生成6位验证码
        return userService.sendCode(phone,session);
    }

    /**
     * 用户登录
     * @param dto
     * @param session
     * @return
     */
    @PostMapping("login")
    public ResponseResult login(@RequestBody UserLoginDto dto,HttpSession session){
        return userService.login(dto,session);
    }

    /**
     * 根据id获取用户
     * @param map
     * @return
     */
    @PostMapping("/getUser")
    public ResponseResult getUser(@RequestBody Map<String, Object> map){
        long userId = Long.parseLong(map.get("userId").toString());
        System.out.println("这里是getUser方法,参数为:"+userId);
        return ResponseResult.okResult(userService.getById(userId));
    }
    /**
     * 更新用户信息
     */
    @PostMapping("/updateUser")
    public ResponseResult updateUser(@RequestBody User user){
        System.out.println("这里是用户更新"+user);
        return ResponseResult.okResult(userService.updateById(user));
    }
    /**
     * 用户上传头像
     */
    @PostMapping("/uploadAvatar")
    public ResponseResult uploadAvatar(MultipartFile file){
        System.out.println("这里接收图片参数"+file);
        return userService.uploadAvatar(file);
    }

    /**
     * 检查身份证号
     */
    @PostMapping("/checkIdNumber")
    public ResponseResult checkIdNumber(@RequestBody IdNumberDto dto){
        System.out.println("这里是要被检查的id"+dto);
        Patient patient = patientService.getOne(Wrappers.<Patient>lambdaQuery()
                .eq(Patient::getIdNumber, dto.getIdNumber()));
        if (patient != null && patient.getUserId() != null){
            return ResponseResult.errorResult(CodeEnum.DATA_EXIST);
        }
        return ResponseResult.okResult("可以使用该用户");

    }

    @Autowired
    private PatientService patientService;

    /**
     * 添加就诊者信息
     * @param patient
     * @return
     */
    @CacheEvict(value = "userPatientList",allEntries = true) //清除所有的缓存数据
    @PostMapping("/savePatient")
    public ResponseResult savePatient(@RequestBody Patient patient){

        Patient oldPatient = patientService.getOne(Wrappers.<Patient>lambdaQuery()
                .eq(Patient::getIdNumber, patient.getIdNumber()));
        //如果是诊所前台已经创建好 但没有进行绑定的  进行绑定
        if (oldPatient != null){
            System.out.println("这里是由旧的用户，但没有绑定"+oldPatient);
            oldPatient.setUserId(BaseContext.getCurrentId());
            patientService.updateById(oldPatient);
            return ResponseResult.okResult("新增成功");
        }
        //完全新创建的就诊者信息
        System.out.println("这里是用户的就诊者信息添加"+patient);
        patient.setCreateTime(LocalDateTime.now());
        patient.setUserId(BaseContext.getCurrentId());
        patient.setUpdateTime(LocalDateTime.now());
        patientService.save(patient);

        return ResponseResult.okResult("新增成功");
    }

    /**
     * 更新就诊者信息
     * @param patient
     * @return
     */
    @PutMapping("/updatePatient")
    @CacheEvict(value = "userPatientList",allEntries = true) //清除所有的缓存数据
    public ResponseResult updatePatient(@RequestBody Patient patient){
        System.out.println("这里是更新就诊者操作"+patient);
        log.info("准备进行更新操作");
        patientService.updateById(patient);
        return ResponseResult.okResult("更新成功");
    }

    /**
     * 用户删除(解绑)就诊者
     * @param patient
     * @return
     */
    @PutMapping("/deletePatient")
    @CacheEvict(value = "userPatientList",allEntries = true) //清除所有的缓存数据
    public ResponseResult deletePatient(@RequestBody Patient patient){
        System.out.println("这里是删除就诊者操作"+ patient);
        log.info("准备进行删除操作",patient.getId());
        patient.setUserId(null);
        patientService.updateById(patient);
        return ResponseResult.okResult("删除成功");
    }


}
