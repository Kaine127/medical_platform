package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.entity.UserAvatar;
import com.mjs.medical.mapper.UserAvatarMapper;
import com.mjs.medical.service.UserAvatarService;
import org.springframework.stereotype.Service;

@Service
public class UserAvatarServiceImpl extends ServiceImpl<UserAvatarMapper, UserAvatar> implements UserAvatarService {
}
