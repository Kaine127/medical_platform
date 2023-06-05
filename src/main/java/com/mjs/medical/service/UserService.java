package com.mjs.medical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.User;
import com.mjs.medical.entity.dto.UserLoginDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

public interface UserService extends IService<User> {

    public ResponseResult sendCode(String phone, HttpSession session);

    public ResponseResult login(UserLoginDto dto,HttpSession session);

    public ResponseResult uploadAvatar(MultipartFile multipartFile);
}
