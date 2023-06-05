package com.mjs.medical.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.druid.support.json.JSONParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.entity.Employee;
import com.mjs.medical.entity.Menu;
import com.mjs.medical.entity.dto.EmployeeDto;
import com.mjs.medical.entity.dto.LoginDto;
import com.mjs.medical.service.EmployeeService;
import com.mjs.medical.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 新增员工
     */
    @PostMapping("/addOrUpdateEmployee")
    public ResponseResult<String> save(HttpServletRequest request,@RequestBody Employee employee){
        System.out.println("这里是员工添加");
        //判断是添加还是更新
        if (employee.getId() == null){
            System.out.println("添加新员工");
            //添加员工
            String salt = RandomUtil.randomString(3);
            employee.setSalt(salt);
            employee.setPassword(DigestUtils.md5DigestAsHex((employee.getPassword() + salt).getBytes()));
            employeeService.save(employee);
            return ResponseResult.okResult(employee);
        }else {
            //修改用户
            //根据id查看原始内容的密码
            System.out.println("员工修改");
            Employee oldOne = employeeService.getById(employee.getId());

            if (oldOne.getPassword().equals(employee.getPassword())){
                //密码一样 直接更新
                employeeService.updateById(employee);
            }else {
                //密码不一样 更新新的密码
                //加密新的用户信息
                String newPassword = DigestUtils.md5DigestAsHex((employee.getPassword() + oldOne.getSalt()).getBytes());
                employee.setPassword(newPassword);
                employeeService.updateById(employee);
            }
            return ResponseResult.okResult("更新成功");
        }


    }


    /**
     * 获取医生列表 用于医生类信息操作
     * @return
     */
    @GetMapping("/doctors")
    public ResponseResult<List<Employee>> doctors(){
        System.out.println("进来了哦");
       //构造条件查询器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        //必须是超级管理和医生
        lambdaQueryWrapper.in(Employee::getAuthority,1,2);
        //必须是正常状态
        lambdaQueryWrapper.eq(Employee::getStatus,1);
        //添加排序
        lambdaQueryWrapper.orderByAsc(Employee::getCreateTime);
        //查询
        List<Employee> doctors = employeeService.list(lambdaQueryWrapper);

        return ResponseResult.okResult(doctors);
    }


    /**
     * 获取诊所员工列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<Page> list(int page,int pageSize,String name){
        //分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Employee::getName,name);
        lambdaQueryWrapper.orderByAsc(Employee::getAuthority).orderByAsc(Employee::getCreateTime);
        employeeService.page(pageInfo,lambdaQueryWrapper);

        return ResponseResult.okResult(pageInfo);
    }


    /**
     * 诊所员工登录
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginDto dto){
        System.out.println("这里进入了登录方法"+dto);
        return employeeService.login(dto);
    }

    @Autowired
    private MenuService menuService;

    /**
     * 根据权限获取列表
     * @param authority
     * @return
     */
    @GetMapping("/permission")
    public ResponseResult permission(@RequestParam("authority") Integer authority){

        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getAuthority,authority);
        lambdaQueryWrapper.orderByAsc(Menu::getLevel);
        List<Menu> menuList = menuService.list(lambdaQueryWrapper);
        System.out.println("menu为:"+menuList);
        List<Object> returnMenuList= new ArrayList<>();
//        Object parse = JSON.parse(menu.getMenuList());
        for(Menu e:menuList){
            System.out.println("进来了"+e.getMenuList()+"接着是"+JSON.parse(e.getMenuList()));
            returnMenuList.add(JSON.parse(e.getMenuList()));
        }
        System.out.println("处理后的menu为:"+returnMenuList);

//        System.out.println("解析后的数组为"+parse+"  其第一项为");;
        return ResponseResult.okResult(returnMenuList);
    }

    /**
     * 医生上传病例图片
     */
    @PostMapping("/uploadCause")
    public ResponseResult uploadCause(MultipartFile file){
        System.out.println("这里接收图片参数"+file);
        return employeeService.uploadCause(file);
    }


    /**
     * 删除员工信息
     * @param dto
     * @return
     */
    @DeleteMapping("/deleteEmployee")
    public ResponseResult deleteEmployee(@RequestBody EmployeeDto dto){
        boolean result = employeeService.removeById(dto.getId());
        if (result){
            return ResponseResult.okResult("删除成功");
        }else {
            return ResponseResult.errorResult(CodeEnum.SERVER_ERROR);
        }
    }

}
