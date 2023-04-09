package com.atguigu.security.filter;

import com.atguigu.security.security.TokenUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义授权处理器/过滤器
 * 继承BasicAuthenticationFilter类即可
 * @author zoutongkun
 */
public class TokenAuthFilter extends BasicAuthenticationFilter {

    private TokenUtils tokenUtils;
    private RedisTemplate redisTemplate;

    public TokenAuthFilter(AuthenticationManager authenticationManager, TokenUtils tokenUtils, RedisTemplate redisTemplate) {
        super(authenticationManager);
        this.tokenUtils = tokenUtils;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 授权过滤器
     * 即当前登录成功的用户的权限加载到springSecurity中备用！！！
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //获取当前认证成功用户权限信息
        UsernamePasswordAuthenticationToken authRequest = getAuthenticationInfo(request);
        //判断如果有权限信息，放到权限上下文中
        if(authRequest != null) {
            SecurityContextHolder.getContext().setAuthentication(authRequest);
        }
        chain.doFilter(request,response);
    }

    /**
     * 根据请求体获取当前用户的用户信息（用户名、token和权限列表）
     */
    private UsernamePasswordAuthenticationToken getAuthenticationInfo(HttpServletRequest request) {
        //从请求的header获取token
        String token = request.getHeader("token");
        if(token != null) {
            //从token获取用户名
            String username = tokenUtils.getUserInfoFromToken(token);
            //从redis获取对应权限列表
            List<String> permissionValueList = (List<String>)redisTemplate.opsForValue().get(username);
            //封装权限列表到springSecurity中，需要使用GrantedAuthority类封装！！！
            Collection<GrantedAuthority> authority = new ArrayList<>();
            for(String permissionValue : permissionValueList) {
                //具体封装使用子类：SimpleGrantedAuthority，一个该对象就封装一个具体权限（其实是包括角色和权限的，但我们最终传入的肯定都是角色下具体的权限啦！）
                SimpleGrantedAuthority auth = new SimpleGrantedAuthority(permissionValue);
                authority.add(auth);
            }
            //最后返回UsernamePasswordAuthenticationToken对象，也即当前用户的用户名，token（不再是密码啦！）和权限列表！！！
            return new UsernamePasswordAuthenticationToken(username,token,authority);
        }
        return null;
    }

}
