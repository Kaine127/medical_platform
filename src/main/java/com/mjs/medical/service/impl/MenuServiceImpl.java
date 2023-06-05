package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.entity.Menu;
import com.mjs.medical.mapper.MenuMapper;
import com.mjs.medical.service.MenuService;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
}
