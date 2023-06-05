package com.mjs.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mjs.medical.entity.PatientCase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CaseMapper extends BaseMapper<PatientCase> {
}
