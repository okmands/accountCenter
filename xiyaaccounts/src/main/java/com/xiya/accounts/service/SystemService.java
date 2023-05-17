package com.xiya.accounts.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.comm.HttpRequest;
import com.xiya.accounts.mapper.account.AccountSystemMapper;
import com.xiya.accounts.mapper.account.SystemMapper;
import com.xiya.accounts.pojo.AccountSystem;
import com.xiya.accounts.pojo.System;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SystemService {

    private final SystemMapper systemMapper;

    private final AccountSystemMapper accountSystemMapper;

    private final RoleService roleService;

    public SystemService(SystemMapper systemMapper,AccountSystemMapper accountSystemMapper,RoleService roleService){
        this.systemMapper = systemMapper;
        this.accountSystemMapper = accountSystemMapper;
        this.roleService = roleService;
    }

    public List<System> getSystemTypeList(String token){
        QueryWrapper<System> systemTypeQuery = new QueryWrapper<>();
        systemTypeQuery.select("sysname,syscode,role_api,api_parameter");
        systemTypeQuery.orderByAsc("syscode");
        List<System> systemTypeList = systemMapper.selectList(systemTypeQuery);
        for (System s : systemTypeList){
            if(s.getSyscode().equals("acc")){
                s.setRole(JSON.parseArray(roleService.getRoleList()));
            }else{
                if(s.getRoleApi() != null){
                    s.setRole(getSystemRole(s.getRoleApi(), s.getApiParameter(),token));
                }
            }
        }
        return systemTypeList;
    }

    public PageInfo<System> getSystemType(Integer current, Integer pageSize){
        QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
        List<AccountSystem> accountSystemList = accountSystemMapper.selectList(accountSystemQuery);
        QueryWrapper<System> systemTypeQuery = new QueryWrapper<>();
        systemTypeQuery.orderByAsc("syscode");
        PageHelper.startPage(current,pageSize);
        List<System> systemList = systemMapper.selectList(systemTypeQuery);
        PageInfo<System> systemPageInfo = new PageInfo<>(systemList);
        List<System> systemTempList = systemPageInfo.getList();
        for (System system : systemList){
            system.setUsercount(accountSystemList.stream().filter(s->s.getSysname().equals(system.getSyscode())).collect(Collectors.toList()).size());
        }
        systemPageInfo.setList(systemTempList);
        return systemPageInfo;
    }

    @Transactional( rollbackFor = Exception.class)
    public void addSystemType(System system) throws Exception {
        try {
            systemMapper.insert(system);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional( rollbackFor = Exception.class)
    public void updateSystemType(System system) throws Exception {
        try {
            systemMapper.updateById(system);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional( rollbackFor = Exception.class)
    public void deleteSystemType(JSONObject jsonObject) throws Exception {
        try {
            JSONArray idList = jsonObject.getJSONArray("idList");
            for (Object o : idList){
                systemMapper.deleteById(o.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    public JSONArray getSystemRole(String roleApi, String apiParameter,String token){
        JSONArray jsonArray = new JSONArray();
        try {
            String result = HttpRequest.sendPost(roleApi,apiParameter,token);
            jsonArray = JSONObject.parseArray(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
