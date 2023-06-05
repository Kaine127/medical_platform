package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.entity.Schedule;
import com.mjs.medical.mapper.ScheduleMapper;
import com.mjs.medical.service.ScheduleService;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
}
