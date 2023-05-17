package com.xiya.accounts.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiya.accounts.mapper.account.*;
import com.xiya.accounts.mapper.hr.PersonMapper;
import com.xiya.accounts.pojo.*;
import com.xiya.accounts.pojo.System;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    @Resource
    private AccountsMapper accountsMapper;
    @Resource
    private AccountSystemMapper accountSystemMapper;
    @Resource
    private PersonMapper personMapper;
    @Resource
    private SystemMapper systemMapper;
    @Resource
    private AccountTokenMapper accountTokenMapper;

    public Accounts getUserProperty(String token) throws Exception {
        //取出对应token的loginid
        QueryWrapper<AccountToken> accountTokenQuery = new QueryWrapper<>();
        accountTokenQuery.eq("token",token);
        AccountToken accountToken = accountTokenMapper.selectOne(accountTokenQuery);
        //如果token没有过期
        if(LocalDateTime.now().isBefore(accountToken.getExpire())){
            //获取工号和名字
            QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
            accountsQuery.select("workid,name,loginid");
            accountsQuery.eq("loginid",accountToken.getLoginid());
            Accounts accounts = accountsMapper.selectOne(accountsQuery);
            //取部门岗位信息
            Person person = personMapper.getPerson(accounts.getWorkid());
            //获取用户被授权登录的系统
            QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
            accountSystemQuery.eq("loginid",accounts.getLoginid());
            accountSystemQuery.orderByAsc("sysname");
            List<AccountSystem> accountSystemList = accountSystemMapper.selectList(accountSystemQuery);
            List<Map<String,Object>> tempList = new ArrayList<>();
            for (AccountSystem accountSystem : accountSystemList){
                Map<String,Object> tempMap = new HashMap<>();
                QueryWrapper<System> systemQuery = new QueryWrapper<>();
                systemQuery.eq("syscode",accountSystem.getSysname());
                System system = systemMapper.selectOne(systemQuery);
                tempMap.put("systemName",system.getSysname());
                tempMap.put("domain",system.getDomain());
                tempMap.put("loginUrl",system.getLoginUrl());
                tempList.add(tempMap);
            }
            //组装账号信息
            accounts.setOrgan(person.getOrgancode());
            accounts.setPost(person.getPost());
            accounts.setAuthSystemList(tempList);
            return accounts;
        }
        throw new Exception("token过期");
    }
}
