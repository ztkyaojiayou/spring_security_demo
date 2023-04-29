package com.atguigu.security.security;

import com.atguigu.base.utils.BaseResponse;
import com.atguigu.base.utils.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登出处理器
 * 实现LogoutHandler接口即可！！！
 * @author zoutongkun
 */
public class TokenLogoutHandler implements LogoutHandler {
  private TokenUtils tokenUtils;
  private RedisTemplate redisTemplate;

  //使用传统的构造器注入，但由于该类没有交由spring管理，因此再使用该类时需要new！！！
  public TokenLogoutHandler(TokenUtils tokenUtils, RedisTemplate redisTemplate) {
    this.tokenUtils = tokenUtils;
    this.redisTemplate = redisTemplate;
  }

  /**
   * 登出
   * @param request
   * @param response
   * @param authentication
   */
  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    // 1 从header里面获取token
    // 2 token不为空，移除token，从redis删除token
    String token = request.getHeader("token");
    if (token != null) {
      // 移除--这一步是不需要的，这里只是为了使逻辑完整而写的！！！
      tokenUtils.removeToken(token);
      // 从token获取用户名
      String username = tokenUtils.getUserInfoFromToken(token);
      //从redis删除token
      //注意：redis中会存储token，也会存储当前用户的权限列表，
      //对于token，key存储的是username，value则为token
      //对于权限列表，key为
      redisTemplate.delete(username);
    }
    //最后，返回信息到前端
    ResponseUtil.out(response, BaseResponse.ok());
  }
}
