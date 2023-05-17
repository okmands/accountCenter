package com.xiya.accounts.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.mapper.account.AccountLogMapper;
import com.xiya.accounts.pojo.AccountLog;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountLogService {
    private final AccountLogMapper accountLogMapper;

    public AccountLogService(AccountLogMapper accountLogMapper){
        this.accountLogMapper = accountLogMapper;
    }

    public PageInfo<AccountLog> getLog(Integer currPage, Integer pageSize, String loginid, JSONArray systemList){
        QueryWrapper<AccountLog> accountLogQuery = new QueryWrapper<>();
        accountLogQuery.like("loginid",loginid);
        accountLogQuery.orderByDesc("action_time");
        if(systemList != null){
            if(systemList.size() > 0)
            {
                List<String> systemTypeList = new ArrayList<>();
                for (Object obj : systemList){
                    systemTypeList.add(obj.toString());
                }
                accountLogQuery.in("syscode",systemTypeList);
            }
        }
        PageHelper.startPage(currPage,pageSize);
        List<AccountLog> accountLogsList = accountLogMapper.selectList(accountLogQuery);
        return new PageInfo<>(accountLogsList);
    }
}
