package com.mjs.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mjs.medical.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
