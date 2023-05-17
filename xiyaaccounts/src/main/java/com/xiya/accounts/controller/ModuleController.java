package com.xiya.accounts.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiya.accounts.comm.Result;
import com.xiya.accounts.pojo.Module;
import com.xiya.accounts.service.ModuleSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/manage/module")
public class ModuleController {


    private final ModuleSerivce moduleSerivce;

    private ModuleController(ModuleSerivce moduleSerivce){
        this.moduleSerivce = moduleSerivce;
    }

    @PostMapping("getModule")
    public String getModule(@RequestBody JSONObject jsonObject){
        try {
            int current = jsonObject.get("current") != null ? jsonObject.getInteger("current") : 1;
            int pageSize = jsonObject.get("pageSize") != null ? jsonObject.getInteger("pageSize") : 10;
            return Result.toSuccess("获取成功",moduleSerivce.getModule(current,pageSize));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("addModule")
    public String addModule(@RequestBody Module module) {
        try {
            moduleSerivce.addModule(module);
            return Result.toSuccess("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("updateModule")
    public String updateModule(@RequestBody Module module){
        try {
            moduleSerivce.updateModule(module);
            return Result.toSuccess("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("deleteModule")
    public String updateModule(@RequestBody List<String> ids){
        try {
            moduleSerivce.deleteModule(ids);
            return Result.toSuccess("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }
}
