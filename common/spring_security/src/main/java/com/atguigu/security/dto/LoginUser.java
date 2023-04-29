package com.atguigu.security.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户登录成功后封装的实体类 需要继承SpringSecurity中的UserDetails类
 *
 * @author zoutongkun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements UserDetails {

  /** 当前登录用户基本信息 */
  /** 微信openid */
  private String username;

  /** 密码 */
  private String password;

  /** 昵称 */
  private String nickName;

  /** 用户头像 */
  private String salt;

  /** 当前用户具备的权限列表 */
  private List<String> permissionList;

  /** SpringSecurity能识别的权限列表！！！
   * 由于UserDTO对象最终需要存储在redis中，默认是需要将该对象中的SimpleGrantedAuthority对象序列化的，
   * 但我们其实没有必要将序列化（因为在没有对redis做序列化配置的时候时在序列化时可能会报错），于是我们使用该注解将其跳过！！！
   * */
@JSONField(serialize = false)
  private Set<SimpleGrantedAuthority> authorities;

  /**
   * 获取当前登录用户的权限列表 主要作用是换成springSecurity支持的权限对象
   *
   * <p>vo转换
   *
   * @return
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 1.先判空
    if (CollectionUtil.isNotEmpty(authorities)) {
      return authorities;
    }
    // 2.为空时才转换
    // 方式1：传统for循环
    //    for (String permissionValue : permissionList) {
    //      if (StringUtils.isEmpty(permissionValue)) continue;
    //      // 转为SimpleGrantedAuthority对象即可~
    //      SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permissionValue);
    //      authorities.add(authority);
    //    }

    // 方式2：使用stream流
    authorities =
        permissionList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
