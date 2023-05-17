package com.xiya.accounts.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiya.accounts.comm.Result;
import com.xiya.accounts.pojo.Role;
import com.xiya.accounts.service.RoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manage/role/")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping("getRole")
    public String getRole(@RequestBody JSONObject jsonObject){
        try {
            Integer current = jsonObject.get("current") == null ? 1 : (Integer) jsonObject.get("current");
            Integer pageSize = jsonObject.get("pageSize") == null ? 20 : (Integer) jsonObject.get("pageSize");
            String search = jsonObject.get("search") == null ? "" : jsonObject.getString("search");
            JSONArray role = jsonObject.get("role") == null ? null : jsonObject.getJSONArray("role");
            return Result.toSuccess("获取成功",roleService.getRole(current,pageSize,search,role));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("获取失败");
        }
    }

    @PostMapping("addRole")
    public String addRole(@RequestBody Role role){
        try {
            roleService.addRole(role);
            return Result.toSuccess("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("updateRole")
    public String updateRole(@RequestBody Role role){
        try {
            roleService.updateRole(role);
            return Result.toSuccess("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("deleteRole")
    public String deleteRole(@RequestBody List<String> ids){
        try {
            roleService.deleteRole(ids);
            return Result.toSuccess("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("getOrgan")
    public String getOrgan(){
        try {
            return Result.toSuccess("获取成功",roleService.getOrgan());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("getModule")
    public String getModule(){
        try {
            return Result.toSuccess("获取成功",roleService.getModule());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("getSystem")
    public String getSystem(){
        try {
            return Result.toSuccess("获取成功",roleService.getSystem());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }
}
