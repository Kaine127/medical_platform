package com.mjs.medical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.Employee;
import com.mjs.medical.entity.dto.LoginDto;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService  extends IService<Employee> {
    public ResponseResult login(LoginDto dto);

    public ResponseResult uploadCause(MultipartFile multipartFile);
}
