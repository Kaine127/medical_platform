package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.entity.CauseMaterial;
import com.mjs.medical.mapper.CauseMaterialMapper;
import com.mjs.medical.service.CauseMaterialService;
import org.springframework.stereotype.Service;

@Service
public class CauseMaterialServiceImpl extends ServiceImpl<CauseMaterialMapper, CauseMaterial> implements CauseMaterialService {
}
