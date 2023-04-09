package com.atguigu.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户登录成功后封装的实体类
 * 需要继承UserDetails类
 * @author zoutongkun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements UserDetails {

    /**
     * 当前登录用户基本信息
     */
    /**
     * 微信openid
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     *用户头像
     */
    private String salt;

    /**
     * 当前用户具备的权限列表
     */
    private List<String> permissionList;

    /**
     * 获取当前登录用户的权限列表
     * 主要作用是换成springSecurity支持的权限对象
     *
     * vo转换
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for(String permissionValue : permissionList) {
            if(StringUtils.isEmpty(permissionValue)) continue;
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permissionValue);
            authorities.add(authority);
        }

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

