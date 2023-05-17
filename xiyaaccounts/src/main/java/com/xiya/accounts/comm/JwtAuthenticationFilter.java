package com.xiya.accounts.comm;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiya.accounts.service.AccountService;
import com.xiya.accounts.service.UserService;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;

public class JwtAuthenticationFilter implements HandlerInterceptor {
    private AccountService accountService;

    public JwtAuthenticationFilter(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        HashMap<String,Object> res = new HashMap<>();
        res.put("code",9999);
        String authStr = request.getHeader("Authorization");
        try {
            DecodedJWT verify = JwtTokenUtil.verify(authStr);
            res.put("code",20000);
            return true;
        }catch (SignatureVerificationException e) {
            e.printStackTrace();//无效签名
            res.put("msg","无效签名");
        }catch (TokenExpiredException e){
            accountService.cleanAccountToken(authStr);
            e.printStackTrace();//token过期
            res.put("msg","token过期");
        }catch (AlgorithmMismatchException e){
            e.printStackTrace();//算法不一致
            res.put("msg","算法不一致");
        }catch (Exception e){
            e.printStackTrace();//token无效
            res.put("msg","token无效");
        }
        String json = new ObjectMapper().writeValueAsString(res);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
