package com.xiya.accounts.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiya.accounts.comm.JwtTokenUtil;
import com.xiya.accounts.comm.PasswordUtil;
import com.xiya.accounts.comm.Result;
import com.xiya.accounts.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manage/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("getUserProperty")
    public String getUserProperty(HttpServletRequest request){
        try {
            return Result.toSuccess("获取成功",userService.getUserProperty(request.getHeader("Authorization")));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }
}
