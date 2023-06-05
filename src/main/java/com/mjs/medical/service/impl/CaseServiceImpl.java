package com.mjs.medical.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.PatientCase;
import com.mjs.medical.entity.DetailCause;
import com.mjs.medical.entity.SimpleCause;
import com.mjs.medical.mapper.CaseMapper;
import com.mjs.medical.service.CaseService;
import com.mjs.medical.service.FileStorageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CaseServiceImpl extends ServiceImpl<CaseMapper, PatientCase> implements CaseService {

    @Autowired
    private FileStorageService fileStorageService;

    // 根据病例信息 分为详细对象和简略对象 配合对应的模板通过freemarker生成静态资源生成存放在minio中
    @Override
    public ResponseResult saveCase(PatientCase patientCase) throws TemplateException, IOException {
        //保存病例信息
        List<String> images = (List<String>) JSON.parse(patientCase.getCauseImages());
        // 创建简略病例
        SimpleCause simpleCause = new SimpleCause();
        simpleCause.setPatientName(patientCase.getPatientName());
        simpleCause.setPatientSex(patientCase.getPatientSex());
        simpleCause.setPatientAge(patientCase.getPatientAge());
        simpleCause.setDoctorName(patientCase.getDoctorName());
        simpleCause.setSimpleCause(patientCase.getSimpleCause());
        // 创建详细病例
        DetailCause detailCause = new DetailCause();
        detailCause.setPatientName(patientCase.getPatientName());
        detailCause.setPatientSex(patientCase.getPatientSex());
        detailCause.setPatientAge(patientCase.getPatientAge());
        detailCause.setDoctorName(patientCase.getDoctorName());
        detailCause.setSimpleCause(patientCase.getSimpleCause());
        detailCause.setDetailCause(patientCase.getDetailCause());
        detailCause.setCauseImages(JSON.toJSONString(images));
        // 用freemarker配合两个对象创建静态页面存放在minio中,并返回对应地址的map
        HashMap<String,String> map = toFreemarker(simpleCause,detailCause);
        patientCase.setSimpleCase(map.get("simpleCause"));
        patientCase.setDetailCase(map.get("detailCause"));
        StringBuilder sb = new StringBuilder(50);
        sb.append(patientCase.getPatientName());
        sb.append("-"+patientCase.getDoctorName()+"-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(new Date());
        sb.append(todayStr);
        String title = sb.toString();
        patientCase.setListTitle(title);
        patientCase.setCreateTime(LocalDateTime.now());
        save(patientCase);

        //修改挂号状态

        return ResponseResult.okResult("病例保存完成");
    }

    @Autowired
    private Configuration configuration;

    public HashMap<String,String> toFreemarker(SimpleCause simpleCause, DetailCause detailCause) throws IOException, TemplateException {
        StringWriter out1 = new StringWriter();
        // 引入静态模板
        Template simpleTemplate = configuration.getTemplate("simpleCase.ftl");
        Template detailTemplate = configuration.getTemplate("detailCase.ftl");
        // 创建map集合存放数值对象 传递给freemarker
        HashMap<String, SimpleCause> simpleMap = new HashMap<>();
        System.out.println("这里是合成操作"+simpleCause);
        simpleMap.put("simplePatientInfo",simpleCause);
        simpleTemplate.process(simpleMap,out1);
        // 创建输入流 读取生成好的静态文件 , 然后转换为输出流 存储到minio中
        InputStream in1 = new ByteArrayInputStream(out1.toString().getBytes());
        String UU1 = RandomUtil.randomString(2);
        // 将静态资源存放到minio中
        String path1 = fileStorageService.uploadHtmlFile(simpleCause.getPatientName()+"-"+UU1+"-",  ".html",in1 );

        StringWriter out2 = new StringWriter();
        HashMap<String, DetailCause> detailMap = new HashMap<>();
        System.out.println("这里是合成操作"+detailCause);
        detailMap.put("detailPatientInfo",detailCause);
        System.out.println(detailMap);
        detailTemplate.process(detailMap,out2);
        InputStream in2 = new ByteArrayInputStream(out2.toString().getBytes());
        String UU2 = RandomUtil.randomString(2);
        String path2 = fileStorageService.uploadHtmlFile(simpleCause.getPatientName()+"-"+UU2+"-",  ".html",in2 );
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("simpleCause",path1);
        map.put("detailCause",path2);

        return map;
    }
}
