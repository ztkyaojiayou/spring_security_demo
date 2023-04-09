package com.atguigu.aclservice.service.auth;

import com.atguigu.aclservice.entity.User;
import com.atguigu.aclservice.service.PermissionService;
import com.atguigu.aclservice.service.UserService;
import com.atguigu.security.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * （核心）数据库中的账号密码校验逻辑！！！
 * 需要实现UserDetailsService接口！！！
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    /**
     *  接收前端的用户名，并获取到当前用户的用户信息和权限信息到springSecurity提供的User类中，供它进行验证！！！
     *  具体的认证逻辑在AbstractUserDetailsAuthenticationProvider--authenticate方法和他的子类DaoAuthenticationProvider中！！！
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询数据
        User curUserInfo = userService.selectByUsername(username);
        //判断
        if(curUserInfo == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        //根据用户查询用户权限列表
        List<String> permissionValueList = permissionService.getPermissionValueByUserId(curUserInfo.getId());
        //需要返回UserDetails类，而我们自定义的SecurityUser即实现了该类，于是返回该类即可！
        UserDTO userDTO = new UserDTO();
        //当前用户的基本信息
        userDTO.setUsername(curUserInfo.getUsername());
        userDTO.setPassword(curUserInfo.getPassword());
        userDTO.setNickName(curUserInfo.getNickName());
        userDTO.setSalt(curUserInfo.getSalt());
        //当前用户的权限列表
        userDTO.setPermissionList(permissionValueList);
        return userDTO;
    }
}
