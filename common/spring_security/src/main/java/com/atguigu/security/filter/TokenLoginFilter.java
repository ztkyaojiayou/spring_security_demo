package com.atguigu.security.filter;

import com.atguigu.security.dto.LoginRequestDTO;
import com.atguigu.security.dto.UserDTO;
import com.atguigu.security.security.TokenUtils;
import com.atguigu.utils.utils.BaseResponse;
import com.atguigu.utils.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 自定义登录/认证处理器
 * 继承UsernamePasswordAuthenticationFilter类即可
 * @author zoutongkun
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private TokenUtils tokenUtils;
    private RedisTemplate redisTemplate;
    private AuthenticationManager authenticationManager;

    /**
     * 构造器注入，同理由于该类没有交由spring管理，因此再使用该类时需要new！！！
     * @param authenticationManager
     * @param tokenUtils
     * @param redisTemplate
     */
    public TokenLoginFilter(AuthenticationManager authenticationManager, TokenUtils tokenUtils, RedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.redisTemplate = redisTemplate;
        this.setPostOnly(false);
        //设置登录时的请求的路径和提交方式--设置为post提交
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/acl/login","POST"));
    }

    /**
     * 1 获取表单提交用户名和密码
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        //获取表单提交数据
        //封装成自定义的user对象！！！
        //那么，明明有默认的实现方法了，为什么还要自己写一套呢？
        //因为默认的获取表单提交的数据只有用户名和密码，但在实际的业务中，一般远远不止这两个参数，
        //因此需要自定义对象来接收该登录请求体！！！
        try {
            LoginRequestDTO loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);
            //返回当前用户的用户名、密码和其对应的权限列表给spring_security备用/管理，它就会去找数据库进行账号和密码的校验，
            //当校验成功时，则会调用下面的successfulAuthentication方法，失败则会调用下面的unsuccessfulAuthentication方法！！！
            //使用UsernamePasswordAuthenticationToken对象返回
            //此时为认证成功状态，这里在构建UsernamePasswordAuthenticationToken时有了权限设置！
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword(),
                    new ArrayList<>()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 2 认证成功调用的方法
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, 
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        //此时认证成功，得到认证成功之后用户信息
        UserDTO user = (UserDTO)authResult.getPrincipal();
        //根据用户名生成token
        String token = tokenUtils.createToken(user.getUsername());
        //把用户名称和用户权限列表放到redis
        //todo token呢？？？不存了？？？
        redisTemplate.opsForValue().set(user.getUsername(),user.getPermissionList());
        //最后将token存入前端浏览器的header中（这里并没有放在cookie中！！！）
        ResponseUtil.out(response, BaseResponse.ok().data("token",token));
    }

    /**
     * 3 认证失败调用的方法
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        //此时就直接返回一个自定义的统一的错误提示即可！！！
        ResponseUtil.out(response, BaseResponse.error());
    }
}
