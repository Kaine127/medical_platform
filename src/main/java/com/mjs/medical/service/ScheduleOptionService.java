package com.mjs.medical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.ScheduleOption;

import java.util.List;

public interface ScheduleOptionService extends IService<ScheduleOption> {

    public List<ScheduleOption> getScheduleOptions(Long doctorId,Long register);

    public List<ScheduleOption> getAddScheduleOptions(Long doctorId);
}
