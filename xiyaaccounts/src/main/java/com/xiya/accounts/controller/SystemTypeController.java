package com.xiya.accounts.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.comm.HttpRequest;
import com.xiya.accounts.comm.Result;
import com.xiya.accounts.pojo.System;
import com.xiya.accounts.service.SystemService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/manage/system")
public class SystemTypeController {
    private final SystemService systemService;

    public SystemTypeController(SystemService systemService){
        this.systemService = systemService;
    }

    @PostMapping(value = "getSystemTypeList")
    public String getSystemTypeList(HttpServletRequest request){
        try {
            List<System> systemList = systemService.getSystemTypeList(request.getHeader("Authorization"));
            return Result.toSuccess("请求成功", systemList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping(value = "getSystemType")
    public String getSystemType(@RequestBody JSONObject jsonObject){
        try {
            Integer current = jsonObject.get("current") == null ? 1 : (Integer) jsonObject.get("current");
            Integer pageSize = jsonObject.get("pageSize") == null ? 20 : (Integer) jsonObject.get("pageSize");
            PageInfo<System> systemTypeList = systemService.getSystemType(current,pageSize);
            return Result.toSuccess("请求成功",systemTypeList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping(value = "addSystemType")
    public String addSystemType(@RequestBody System system){
        try {
            systemService.addSystemType(system);
            return Result.toSuccess("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("添加失败");
        }
    }

    @PostMapping(value = "updateSystemType")
    public String updateSystemType(@RequestBody System system){
        try {
            systemService.updateSystemType(system);
            return Result.toSuccess("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("更新失败");
        }
    }

    @PostMapping(value = "deleteSystemType")
    public String deleteSystemType(@RequestBody JSONObject jsonObject){
        try {
            systemService.deleteSystemType(jsonObject);
            return Result.toSuccess("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail("删除失败");
        }
    }
}
