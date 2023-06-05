package com.mjs.medical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.Register;
import com.mjs.medical.entity.dto.DoctorDto;
import com.mjs.medical.entity.dto.RegisterPatientDto;
import com.mjs.medical.entity.dto.ScheduleOnlineDto;
import org.springframework.web.bind.annotation.RequestBody;

public interface RegisterService extends IService<Register> {
    public ResponseResult getRegisterDoctor();
    public ResponseResult getRegisterList(DoctorDto dto);
    public ResponseResult doRegister(ScheduleOnlineDto dto);
    public ResponseResult getDoctorName();
    public ResponseResult registerNow(RegisterPatientDto dto);
}
