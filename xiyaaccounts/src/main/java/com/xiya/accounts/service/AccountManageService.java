package com.xiya.accounts.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.comm.PasswordUtil;
import com.xiya.accounts.mapper.account.AccountSystemMapper;
import com.xiya.accounts.mapper.account.AccountTokenMapper;
import com.xiya.accounts.mapper.account.AccountsMapper;
import com.xiya.accounts.mapper.hr.PersonMapper;
import com.xiya.accounts.pojo.AccountSystem;
import com.xiya.accounts.pojo.AccountToken;
import com.xiya.accounts.pojo.Accounts;
import com.xiya.accounts.pojo.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountManageService {

    private final AccountsMapper accountsMapper;
    private final AccountSystemMapper accountSystemMapper;
    private final PersonMapper personMapper;
    private final AccountTokenMapper accountTokenMapper;

    public AccountManageService(AccountsMapper accountsMapper,AccountSystemMapper accountSystemMapper,PersonMapper personMapper,AccountTokenMapper accountTokenMapper){
        this.accountsMapper = accountsMapper;
        this.accountSystemMapper = accountSystemMapper;
        this.personMapper = personMapper;
        this.accountTokenMapper = accountTokenMapper;
    }

    public PageInfo<Accounts> getAccount(Integer current, Integer pageSize, String search, JSONArray authSystemList){
        QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
        QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
        //如果有系统类型查询条件，查找符合系统类型条件的登陆id
        if(authSystemList != null){
            if(authSystemList.size() > 0)
            {
                List<String> systemTypeList = new ArrayList<>();
                for (Object obj : authSystemList){
                    systemTypeList.add(obj.toString());
                }
                accountSystemQuery.in("sysname",systemTypeList);
                List<AccountSystem> systemLoginidList = accountSystemMapper.selectList(accountSystemQuery);
                List<String> tempLoginidList = new ArrayList<>();
                //提取登录id用于在账号列表查询
                for (AccountSystem item : systemLoginidList){
                    tempLoginidList.add(item.getLoginid());
                }
                accountsQuery.in("loginid",tempLoginidList.stream().distinct().collect(Collectors.toList()));
            }
        }
        accountsQuery.and(a->a.like("workid",search).or(b->b.like("name",search)));

        PageHelper.startPage(current,pageSize);
        PageInfo<Accounts> accountsPageInfo = new PageInfo<>(accountsMapper.selectList(accountsQuery));
        List<Accounts> tempAccountsList = accountsPageInfo.getList();
        //组装数据
        for (Accounts item : tempAccountsList){
            Person person = personMapper.getPerson(item.getWorkid());
            item.setOrgan(person.getOrgancode());
            item.setPost(person.getPost());
            item.setStatus(person.getAccountLock().equals("0") ? "正常" : "禁用");
            accountSystemQuery.clear();
            accountSystemQuery.eq("loginid",item.getLoginid());
            accountSystemQuery.orderByAsc("sysname");
            List<AccountSystem> asList = accountSystemMapper.selectList(accountSystemQuery);
            List<Map<String,Object>> tempList = new ArrayList<>();
            for (AccountSystem as : asList){
                Map<String,Object> tempMap = new HashMap<>();
                tempMap.put("system",as.getSysname());
                tempMap.put("role",as.getRole());
                tempMap.put("roleName",as.getRoleName());
                tempList.add(tempMap);
            }
            item.setAuthSystemList(tempList);
        }
        accountsPageInfo.setList(tempAccountsList);
        return accountsPageInfo;
    }

    @Transactional( rollbackFor = Exception.class)
    public void updateAccount(Accounts accounts) throws Exception {
        try {
            Accounts updateAccounts = new Accounts();
            updateAccounts.setEnddate(accounts.getEnddate());
            UpdateWrapper<Accounts> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("guid",accounts.getGuid());
            accountsMapper.update(updateAccounts, updateWrapper);

            Person person = new Person();
            person.setAccountLock(accounts.getStatus().equals("正常") ? "0" : "1");
            UpdateWrapper<Person> personUpdate = new UpdateWrapper<>();
            personUpdate.eq("jobnum",accounts.getWorkid());
            personMapper.update(person,personUpdate);

            //先清除所有授权登录的系统
            QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
            accountSystemQuery.eq("loginid",accounts.getLoginid());
            accountSystemMapper.delete(accountSystemQuery);
            //重新再添加新授权的系统
            List<Map<String,Object>> authSystemList = accounts.getAuthSystemList();
            for(Map<String,Object> s : authSystemList) {
                AccountSystem accountSystem = new AccountSystem();
                accountSystem.setLoginid(accounts.getLoginid());
                accountSystem.setSysname(s.get("system").toString());
                accountSystem.setRole(s.get("role") != null ? s.get("role").toString() : null);
                accountSystem.setRoleName(s.get("roleName") != null ? s.get("roleName").toString() : null);
                accountSystemMapper.insert(accountSystem);
            }
        } catch(Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    public int verifyWorkid(Accounts accounts){
        int res = 0;
        Person person = personMapper.getPerson(accounts.getWorkid());
        if(person != null){
            res = 65;
            if(person.getStatus().equals("-90") || person.getStatus().equals("90")){
                res = 90;
            }else{
                QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
                accountsQuery.eq("workid",person.getJobnum());
                accountsQuery.eq("name",person.getSname());
                if(accountsMapper.selectOne(accountsQuery) != null){
                    res = 1;
                }
            }
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAccount(Accounts accounts){
        try {
            accounts.setPwd(PasswordUtil.genPassword(accounts.getLoginid(),"Xiya@111"));
            accounts.setCreatedate(LocalDateTime.now());
            accountsMapper.insert(accounts);

            Person person = new Person();
            person.setAccountLock("0");
            UpdateWrapper<Person> personUpdate = new UpdateWrapper<>();
            personUpdate.eq("jobnum",accounts.getWorkid());
            personMapper.update(person,personUpdate);

            //添加授权的系统
            if(accounts.getAuthSystemList() != null){
                List<Map<String,Object>> authSystemList = accounts.getAuthSystemList();
                for(Map<String,Object> s : authSystemList) {
                    AccountSystem accountSystem = new AccountSystem();
                    accountSystem.setLoginid(accounts.getLoginid());
                    accountSystem.setSysname(s.get("system").toString());
                    accountSystem.setRole(s.get("role") != null ? s.get("role").toString() : null);
                    accountSystem.setRoleName(s.get("roleName") != null ? s.get("roleName").toString() : null);
                    accountSystemMapper.insert(accountSystem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(JSONObject jsonObject) throws Exception {
        try {
            JSONArray guidArray = jsonObject.getJSONArray("guids");
            if(guidArray.size() > 0){
                for (Object o : guidArray){
                    //通过guid取出loginid
                    QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
                    accountsQuery.eq("guid",o.toString());
                    //开始重置密码
                    UpdateWrapper<Accounts> accountsUpdate = new UpdateWrapper<>();
                    accountsUpdate.eq("guid",o.toString());
                    Accounts accounts = new Accounts();
                    accounts.setPwd(PasswordUtil.genPassword(accountsMapper.selectOne(accountsQuery).getLoginid(),"Xiya@111"));
                    accountsMapper.update(accounts,accountsUpdate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(JSONObject jsonObject) throws Exception {
        try {
            JSONArray guidArray = jsonObject.getJSONArray("guids");
            if(guidArray.size() > 0){
                for (Object o : guidArray){
                    //通过guid取出loginid
                    QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
                    accountsQuery.eq("guid",o.toString());
                    String loginid = accountsMapper.selectOne(accountsQuery).getLoginid();
                    accountsMapper.delete(accountsQuery);
                    QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
                    accountSystemQuery.eq("loginid",loginid);
                    accountSystemMapper.delete(accountSystemQuery);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    public AccountToken authToken(String token){
        QueryWrapper<AccountToken> accountTokenQuery = new QueryWrapper<>();
        accountTokenQuery.eq("token",token);
        return accountTokenMapper.selectOne(accountTokenQuery);
    }
}
