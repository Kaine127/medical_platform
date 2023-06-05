package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.entity.Patient;
import com.mjs.medical.mapper.PatientMapper;
import com.mjs.medical.service.PatientService;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
}
