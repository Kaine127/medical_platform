package com.mjs.medical.common;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.UserException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理
     * @return
     */
    // sql异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseBody
    public ResponseResult exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return ResponseResult.errorResult(1000,msg);
        }

        return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
    }

    /**
     * 用户操作自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(UserExeception.class)
    @ResponseBody
    public ResponseResult exceptionHandler(UserExeception e){
        log.error("catch exception:{}",e);



        return ResponseResult.errorResult(e.getCodeEnum());
    }



}