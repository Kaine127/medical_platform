package com.mjs.medical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.PatientCase;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface CaseService extends IService<PatientCase> {

    public ResponseResult saveCase(PatientCase patientCase) throws TemplateException, IOException;
}
