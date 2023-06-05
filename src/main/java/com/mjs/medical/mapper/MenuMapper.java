package com.mjs.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mjs.medical.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
}
