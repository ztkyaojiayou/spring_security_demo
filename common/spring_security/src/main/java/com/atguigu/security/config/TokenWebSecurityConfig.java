package com.atguigu.security.config;

import com.atguigu.security.filter.TokenAuthFilter;
import com.atguigu.security.filter.TokenLoginFilter;
import com.atguigu.security.security.DefaultPasswordEncoder;
import com.atguigu.security.security.TokenLogoutHandler;
import com.atguigu.security.security.TokenUtils;
import com.atguigu.security.security.UnAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 核心配置类，参考demo1中的注释
 * 主要用于查库比对！！！
 * @author zoutongkun
 */
@Configuration
@EnableWebSecurity
//需要先开启权限注解！！！
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private TokenUtils tokenUtils;
    private RedisTemplate redisTemplate;
    private DefaultPasswordEncoder defaultPasswordEncoder;
    //自定义账号密码校对逻辑，也即走数据库查询！！！
    private UserDetailsService userDetailsService;

    @Autowired
    public TokenWebSecurityConfig(UserDetailsService userDetailsService, DefaultPasswordEncoder defaultPasswordEncoder,
                                  TokenUtils tokenUtils, RedisTemplate redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.tokenUtils = tokenUtils;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 配置设置
     * @param http
     * @throws Exception
     */
    //设置退出的地址和token，redis操作地址
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new UnAuthEntryPoint())//没有权限访问时执行的逻辑
                .and().csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/admin/acl/index/logout")//登出路径
                .addLogoutHandler(new TokenLogoutHandler(tokenUtils,redisTemplate)).and()
                .addFilter(new TokenLoginFilter(authenticationManager(), tokenUtils, redisTemplate))//使用自定义的登录/认证处理器
                .addFilter(new TokenAuthFilter(authenticationManager(), tokenUtils, redisTemplate)).httpBasic();//使用自定义的授权处理器/过滤器
    }

    //调用userDetailsService和密码处理
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }
    //不进行认证的路径，即该路径可以直接访问
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/api/**");
    }
}
