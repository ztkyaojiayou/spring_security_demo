package com.atguigu.security.security;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * token根工具类
 */
@Component
public class TokenUtils {
    //token有效时长
    private long tokenExpiration = 24*60*60*1000;
    //编码秘钥
    private String tokenSignKey = "123456";

    /**
     * 1.使用jwt根据用户名生成token
     * @param username
     * @return
     */
    public String createToken(String username) {
        String token = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+ tokenExpiration))
                .signWith(SignatureAlgorithm.HS512, tokenSignKey).compressWith(CompressionCodecs.GZIP).compact();
        return token;
    }

    /**
     * 根据token字符串得到用户名
     * @param token
     * @return
     */
    public String getUserInfoFromToken(String token) {
        String username = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token).getBody().getSubject();
        return username;
    }

    /**
     * 删除token：不需要删（不携带即可），也删除不了，只能等过期，这也是使用token的弊端！！！
     * @param token
     */
    public void removeToken(String token) { }
}
