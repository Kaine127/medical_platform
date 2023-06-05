package com.mjs.medical.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.PatientCase;
import com.mjs.medical.entity.dto.PatientDto;
import com.mjs.medical.service.CaseService;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 病例生成相关类
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/case")
@Slf4j
public class CaseController {

    @Autowired
    private CaseService caseService;

    /**
     * 保存病例信息 分为简略病例和详细病例
     * @param patientCase
     * @return
     * @throws TemplateException
     * @throws IOException
     */
    @PostMapping("/saveCase")
    public ResponseResult saveCase(@RequestBody PatientCase patientCase) throws TemplateException, IOException {
        System.out.println("到了saveCase了");
        return caseService.saveCase(patientCase);
    }

    /**
     * 根据id返回所有的病例信息
     * @param dto
     * @return
     */
    @PostMapping("/getPatientCase")
    public ResponseResult getPatientCase(@RequestBody PatientDto dto){
        List<PatientCase> patientCaseList = caseService.list(Wrappers.<PatientCase>lambdaQuery().eq(PatientCase::getPatientId, dto.getPatientId()).orderByDesc(PatientCase::getCreateTime));
        return ResponseResult.okResult(patientCaseList);
    }
}
