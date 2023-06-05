package com.mjs.medical.filter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.mjs.medical.common.BaseContext;
import com.mjs.medical.common.CodeEnum;
import com.mjs.medical.common.ResponseResult;
import com.mjs.medical.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginFilter",urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //如果是cors的试错方法放行
        if (request.getMethod().equals("OPTIONS")){
            log.info("本次请求是opttions不需要处理");
            filterChain.doFilter(request,response);
            return;
        }
        System.out.println("查看是否有token"+request.getHeader("token"));

        //1. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求: {}",requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/user/sms", //移动端发送短信
                "/user/login",
                "/employee/uploadCause",
                "/register/getRegisterDoctor",
                "/task/doTask"

        };
        //2. 判断本次请求是否需要处理
        Boolean check = check(urls, requestURI);

        //3. 如果不需要处理, 则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }


        //4-1. 判断登录状态, 如果已经登录, 则直接放行
        //获取token
        String token = request.getHeader("token");
        if(!StringUtils.isBlank(token)){
            System.out.println("存在token");
            Claims claimsBody = JwtUtil.getClaimsBody(token);
            //判断token是否过期
            int result = JwtUtil.verifyToken(claimsBody);
            if(result ==1 || result == 2){
                System.out.println("这里是过期了");
                response.getWriter().write(JSON.toJSONString(ResponseResult.errorResult(CodeEnum.TOKEN_EXPIRE)));
                return;
            }
            //token存在 且 未过期
            System.out.println("是转换的问题嘛");
            Long id = Long.valueOf(claimsBody.get("id").toString());
            log.info("用户已登录,用户id为:{}",id);
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }


        //5. 如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(ResponseResult.errorResult(CodeEnum.NEED_LOGIN)));

        return;
    }

    public Boolean check(String[] urls,String requestUrl){
        for (String url : urls) {
            if(PATH_MATCHER.match(url,requestUrl)){
                return true;
            }
        }
        return  false;
    }
}
