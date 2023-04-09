package com.atguigu.security.security;

import com.atguigu.utils.utils.BaseResponse;
import com.atguigu.utils.utils.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未授权接口的统一处理类
 * 实现AuthenticationEntryPoint接口即可！！！
 * @author zoutongkun
 */
public class UnAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) {
        //返回自定义的统一的error()方法即可！！！
        ResponseUtil.out(httpServletResponse, BaseResponse.error());
    }
}
