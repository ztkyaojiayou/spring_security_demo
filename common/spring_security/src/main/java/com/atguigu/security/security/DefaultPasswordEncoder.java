package com.atguigu.security.security;

import com.atguigu.base.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 对前端传入的密码进行自定义加密和比对处理
 * 实现PasswordEncoder接口即可
 * @author zoutongkun
 */
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    public DefaultPasswordEncoder() {
        this(-1);
    }
    public DefaultPasswordEncoder(int strength) {
    }

    /**
     * 对前端传入的密码进行MD5加密
     * @param charSequence
     * @return
     */
    @Override
    public String encode(CharSequence charSequence) {
        return MD5.encrypt(charSequence.toString());
    }

    /**
     * 进行密码比对
     * @param rawPassword 前端传入的原密码
     * @param encodedPassword 加密后的密码
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }
}
