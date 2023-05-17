package com.xiya.accounts.comm;

import com.auth0.jwt.JWTCreator;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtTokenUtil {

    //自定密钥，最好搞长一点
    public static final String tokenKey = "xiya3333";
    /*
    生成票证
     */
    public static String getSign(HashMap<String,Object> headMap, HashMap<String,String> claimMap, int hours){
        LocalDateTime localDateTime = LocalDateTime.now().plus(hours, ChronoUnit.HOURS);
        Date date = localDateTimeToDate(localDateTime);
        JWTCreator.Builder builder = JWT.create().withHeader(headMap);
        claimMap.forEach((key, value) -> {
            builder.withClaim(key, value);
        });
        builder.withExpiresAt(date);
        String token = builder.sign(Algorithm.HMAC256(tokenKey));
        return token;
    }

    /*
    获取token信息，token不对会异常
     */
    public static DecodedJWT verify(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(tokenKey)).build().verify(token);
        return verify;
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            return Date.from(zdt.toInstant());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

