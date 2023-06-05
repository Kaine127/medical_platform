package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.common.BaseContext;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.common.UserExeception;
import com.mjs.medical.entity.CauseMaterial;
import com.mjs.medical.entity.Employee;
import com.mjs.medical.entity.UserAvatar;
import com.mjs.medical.entity.dto.LoginDto;
import com.mjs.medical.mapper.EmployeeMapper;
import com.mjs.medical.service.CauseMaterialService;
import com.mjs.medical.service.EmployeeService;
import com.mjs.medical.service.FileStorageService;
import com.mjs.medical.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    public ResponseResult login(LoginDto dto){

        //1.正常登录
        if (!StringUtils.isBlank(dto.getUsername())&&!StringUtils.isBlank(dto.getPassword())){
            //1.1查询用户
            Employee employee = getOne(Wrappers.<Employee>lambdaQuery()
                    .eq(Employee::getUsername, dto.getUsername()));
            if (employee == null){
                return ResponseResult.errorResult(CodeEnum.DATA_EXIST,"该员工不存在");
            }
            //1.2对比密码
            String salt = employee.getSalt();
            String inputPassword = dto.getPassword();
            inputPassword = DigestUtils.md5DigestAsHex((inputPassword + salt).getBytes());
            System.out.println("这里用于获取加密后的数据"+ inputPassword);
            //1.2.1验证密码
            if (!inputPassword.equals(employee.getPassword())){
                 return ResponseResult.errorResult(CodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //1.3 登录成功返回数据 jwt
            Map<String,Object> map = new HashMap<>();
            map.put("token", JwtUtil.getToken(Long.valueOf(employee.getId())));


//            Claims claims = JwtUtil.getClaimsBody(JwtUtil.getToken(Long.valueOf(employee.getAuthority())));
//            System.out.println(claims.get("id"));


            //1.3.1 将盐值和密码置为空
            employee.setSalt("");
            employee.setPassword("");
            map.put("user",employee);
            System.out.println("这里是成返回的地方"+map);
            return ResponseResult.okResult(map);
        }else {
            return ResponseResult.errorResult(CodeEnum.PARAM_REQUIRE,"缺少参数");
        }

    }

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CauseMaterialService causeMaterialService;

    @Override
    public ResponseResult uploadCause(MultipartFile multipartFile) {
        System.out.println("这里是上传前参数"+multipartFile);
        //1.检查参数
        if(multipartFile == null || multipartFile.getSize() == 0){
            throw new UserExeception(CodeEnum.PARAM_INVALID);
        }
        //查询id
        System.out.println("上传前查询通过");
        //2.上传图片到minIO中
        //获取图片名
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //获取后缀 先判断是否有后缀
        String originalFilename = multipartFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        if (index != -1){
            //存在后缀
            String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
            fileName = fileName + postfix;

        }

        String uploadImgFile = null;
        //存入minio
        try {
            uploadImgFile = fileStorageService.uploadImgFile("", fileName, multipartFile.getInputStream());
            log.info("图片存入minio,路径为:{}",uploadImgFile);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("图片上传失败");
            throw new UserExeception(CodeEnum.SERVER_ERROR);//抛出异常作用：1.事务回滚 2.通过全局异常处理器向前端返回异常提示
        }

        //存入病因素材表
        try {
            CauseMaterial causeMaterial = new CauseMaterial();
            causeMaterial.setCreateTime(LocalDateTime.now());
            causeMaterial.setUrl(uploadImgFile);
            causeMaterialService.save(causeMaterial);
            return ResponseResult.okResult(causeMaterial);
        }catch (Exception e){
            //如果出错 , 删除minio中刚上传的文件
            fileStorageService.delete(uploadImgFile);
            throw e;
        }
    }

}
