package com.mjs.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mjs.medical.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
