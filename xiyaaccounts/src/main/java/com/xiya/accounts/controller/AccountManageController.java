package com.xiya.accounts.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.comm.Result;
import com.xiya.accounts.pojo.Accounts;
import com.xiya.accounts.pojo.Person;
import com.xiya.accounts.service.AccountManageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/manage/account")
public class AccountManageController {

    private final AccountManageService accountManageService;

    public AccountManageController(AccountManageService accountManageService) {
        this.accountManageService = accountManageService;
    }

    @PostMapping(value = "getAccount")
    public String getAccount(@RequestBody JSONObject jsonObject){
        try {
            Integer current = jsonObject.get("current") == null ? 1 : (Integer) jsonObject.get("current");
            Integer pageSize = jsonObject.get("pageSize") == null ? 20 : (Integer) jsonObject.get("pageSize");
            String search = jsonObject.get("search") == null ? "" : jsonObject.getString("search");
            JSONArray authSystemList = jsonObject.get("authSystemList") == null ? null : jsonObject.getJSONArray("authSystemList");
            PageInfo<Accounts> accountsPageInfo = accountManageService.getAccount(current,pageSize,search,authSystemList);
            return Result.toSuccess("请求成功",accountsPageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping(value = "updateAccount")
    public String updateAccount(@RequestBody Accounts accounts){
        try {
            accountManageService.updateAccount(accounts);
            return Result.toSuccess("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("更新失败" + e.getMessage());
        }
    }

    @PostMapping(value = "addAccount")
    public String addAccount(@RequestBody Accounts accounts){
        try {
            accountManageService.addAccount(accounts);
            return Result.toSuccess("添加成功");
        } catch (Exception e){
            e.printStackTrace();
            return Result.toFail("添加失败" + e.getMessage());
        }
    }

    @PostMapping(value = "resetPassword")
    public String resetPassword(@RequestBody JSONObject jsonObject) {
        try {
            accountManageService.resetPassword(jsonObject);
            return Result.toSuccess("重置密码成功,新的密码为\"Xiya@111\"");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("重置密码失败" + e.getMessage());
        }
    }

    @PostMapping(value = "deleteAccount")
    public String deleteAccount(@RequestBody JSONObject jsonObject){
        try {
            accountManageService.deleteAccount(jsonObject);
            return Result.toSuccess("删除账号成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("删除账号失败" + e.getMessage());
        }
    }

    @PostMapping(value = "verifyWorkid")
    public String verifyWorkid(@RequestBody Accounts accounts){
        try {
            int res = accountManageService.verifyWorkid(accounts);
            switch (res){
                case 0:
                    return Result.toSuccess("该工号不存在,请核实后再试!");
                case 90 :
                    return Result.toSuccess("该工号已离职,请核实后再试!");
                case 1 :
                    return Result.toSuccess("该账号已存在，请直接编辑账号进行操作");
                default:
                    return Result.toSuccess("验证工号成功",accountManageService.verifyWorkid(accounts));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("验证工号失败" + e.getMessage());
        }
    }

    @PostMapping("authToken")
    public String authToken(HttpServletRequest request, @RequestBody JSONObject jsonObject){
        String token = request.getHeader("Authorization");
        String sysType = jsonObject.get("sysType") != null ? jsonObject.getString("sysType") : null;
        try {
            return Result.toSuccess("认证成功",accountManageService.authToken(token));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }
}
