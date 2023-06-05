package com.mjs.medical.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mjs.medical.common.BaseContext;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.common.UserExeception;
import com.mjs.medical.entity.Employee;
import com.mjs.medical.entity.User;
import com.mjs.medical.entity.UserAvatar;
import com.mjs.medical.entity.dto.UserLoginDto;
import com.mjs.medical.mapper.UserMapper;
import com.mjs.medical.service.FileStorageService;
import com.mjs.medical.service.UserAvatarService;
import com.mjs.medical.service.UserService;
import com.mjs.medical.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import cn.hutool.core.util.RandomUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 生成验证码
     * @param phone
     * @param session
     * @return
     */
    @Override
    public ResponseResult sendCode(String phone, HttpSession session) {
        String code =  RandomUtil.randomNumbers(6);
//      1.保存验证码到Redis  在采用Redis中采用String的数据结构  "login:code:"+phone这样更加有层次感
//      2, TimeUnit.MINUTES有效期是两分钟  类似set key value ex(有效期，单位是秒）
        System.out.println("这里是设置redis之前");
        stringRedisTemplate.opsForValue().set("login:code:"+phone,code,2, TimeUnit.MINUTES);

        //3.发送验证码
        System.out.println("已通过第三方短信服务平台发送数据,验证码为:"+code);
        return ResponseResult.okResult("短信已发送");
    }

    @Override
    public ResponseResult login(UserLoginDto dto, HttpSession session) {
        //1.正常登录
        if(!StringUtils.isBlank(dto.getPhone())
                && !StringUtils.isBlank(dto.getCode())){
            //1.1 拿取两种验证码
            String cacheCode = stringRedisTemplate.opsForValue().get("login:code:"+dto.getPhone());
            String code = dto.getCode();
            //1.2 对比验证码
            if(cacheCode ==null || !cacheCode.equals(code)){
                return ResponseResult.errorResult(CodeEnum.PARAM_INVALID);
            }
            //1.3 验证成功，进行用户操作
            //1.4先验证是否存在用户
            User user = this.getOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getPhone, dto.getPhone()));
            //1.5 判断之前没有该用户了
            if (user == null){
                System.out.println("这里是没有用户该创建了");
                // 没有用户 创建新的
                user = new User();
                user.setPhone(dto.getPhone());
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                //保存后自动返回主键
                System.out.println("这里该保存了");
                this.save(user);
            }
            //1.6 生成token
            Map<String,Object> map = new HashMap<>();
            String token = JwtUtil.getToken(user.getId());
            map.put("token",token);
            map.put("user",user);
            //1.7返回token和user对象
            return ResponseResult.okResult(map);

        }else {
            //2 登录失败 缺少手机号 或 验证码
            return ResponseResult.errorResult(CodeEnum.PARAM_REQUIRE);
        }
    }

    @Autowired
    private UserAvatarService userAvatarService;

    @Autowired
    private FileStorageService fileStorageService;
    /**
     * 上传用户头像
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadAvatar(MultipartFile multipartFile) {
        System.out.println("这里是上传前参数"+multipartFile);
        //1.检查参数
        if(multipartFile == null || multipartFile.getSize() == 0){
            throw new UserExeception(CodeEnum.PARAM_INVALID);
        }
        //查询id
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null){
            throw new UserExeception(CodeEnum.NEED_LOGIN);
        }
        System.out.println("上传前查询通过");
        //2.上传图片到minIO中
        //获取图片名
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //获取后缀 先判断是否有后缀
        String originalFilename = multipartFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        if (index != -1){
            //存在后缀
            String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
            fileName = fileName + postfix;

        }

        String uploadImgFile = null;
        //存入minio
        try {
            uploadImgFile = fileStorageService.uploadImgFile("", fileName, multipartFile.getInputStream());
            log.info("图片存入minio,路径为:{}",uploadImgFile);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("图片上传失败");
            throw new UserExeception(CodeEnum.SERVER_ERROR);//抛出异常作用：1.事务回滚 2.通过全局异常处理器向前端返回异常提示
        }
        //存入头像表
        try {
            UserAvatar userAvatar = new UserAvatar();
            userAvatar.setUserId(currentId);
            userAvatar.setUrl(uploadImgFile);
            userAvatar.setCreateTime(LocalDateTime.now());
            System.out.println("这里是存放前");
            userAvatarService.save(userAvatar);
            return ResponseResult.okResult(userAvatar);
        }catch (Exception e){
            //如果出错 , 删除minio中刚上传的文件
            fileStorageService.delete(uploadImgFile);
            throw e;
        }

    }
}
