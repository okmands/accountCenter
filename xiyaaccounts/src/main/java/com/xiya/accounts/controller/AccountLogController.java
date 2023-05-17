package com.xiya.accounts.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.comm.Result;
import com.xiya.accounts.pojo.AccountLog;
import com.xiya.accounts.service.AccountLogService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/manage/accountLog")
public class AccountLogController {
    private final AccountLogService accountLogService;

    public AccountLogController(AccountLogService accountLogService){
        this.accountLogService = accountLogService;
    }

    @PostMapping(value = "getLog")
    public String getLog(@RequestBody JSONObject jsonObject){
        try {
            Integer current = jsonObject.get("current") == null ? 1 : (Integer) jsonObject.get("current");
            Integer pageSize = jsonObject.get("pageSize") == null ? 20 : (Integer) jsonObject.get("pageSize");
            String loginid = jsonObject.get("loginid") == null ? "" : jsonObject.getString("loginid");
            JSONArray systemList = jsonObject.get("systemList") == null ? null : jsonObject.getJSONArray("systemList");
            PageInfo<AccountLog> accountLogPageInfo = accountLogService.getLog(current,pageSize,loginid,systemList);
            return Result.toSuccess("获取成功",accountLogPageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }
}
