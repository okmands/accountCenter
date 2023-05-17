package com.xiya.accounts.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xiya.accounts.comm.PasswordUtil;
import com.xiya.accounts.mapper.account.*;
import com.xiya.accounts.mapper.hr.PersonMapper;
import com.xiya.accounts.pojo.*;
import com.xiya.accounts.pojo.System;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountService {

    @Resource
    private AccountsMapper accountsMapper;
    @Resource
    private AccountSystemMapper accountSystemMapper;
    @Resource
    private AccountLogMapper accountLogMapper;
    @Resource
    private SystemMapper systemMapper;
    @Resource
    private PersonMapper personMapper;
    @Resource
    private AccountTokenMapper accountTokenMapper;
    @Resource
    private RoleMapper roleMapper;

    public Map<String, Object> login(String syscode, String loginid, String password) {
        Map<String, Object> resMap = new HashMap<>();
        try {
            QueryWrapper<System> systemQuery = new QueryWrapper<>();
            systemQuery.eq("syscode", syscode);
            System system = systemMapper.selectOne(systemQuery);
            if (system.getStatus().equals("1")) {
                resMap.put("msg", "系统维护中,请稍后再试");
                resMap.put("code", "0");
            } else if (system.getStatus().equals("2")) {
                resMap.put("msg", "系统故障,请稍后再试");
                resMap.put("code", "0");
            } else {
                //获取账号信息
                QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
                accountsQuery.eq("loginid", loginid);
                Accounts accounts = accountsMapper.selectOne(accountsQuery);
                //获取账号可登录的系统列表
                QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
                accountSystemQuery.eq("loginid", loginid);
                List<AccountSystem> accountSystemList = accountSystemMapper.selectList(accountSystemQuery);
                if (accounts == null) {
                    insertAccountLog(loginid, "未找到该账号", "登录", syscode);
                    resMap.put("msg", "未找到该账号");
                    resMap.put("code", "0");
                } else {
                    Person person = personMapper.getPerson(accounts.getWorkid());
                    if(person.getAccountLock().equals("1")){
                        insertAccountLog(loginid, "账号状态为禁用", "登录", syscode);
                        if(person.getStatus().equals("90") || person.getStatus().equals("-90")){
                            accountsMapper.delete(accountsQuery);
                            accountSystemMapper.delete(accountSystemQuery);
                        }
                        resMap.put("msg", "账号状态为禁用");
                        resMap.put("code", "0");
                    }else if (person.getAccountLock().equals("0") && (person.getStatus().equals("65") || person.getStatus().equals("68"))){
                        if (new Date().compareTo(accounts.getEnddate()) > 0) {
                            insertAccountLog(loginid, "账号已过有效期", "登录", syscode);
                            resMap.put("msg", "账号已过有效期");
                            resMap.put("code", "0");
                        } else {
                            if (!accounts.getPwd().equals(PasswordUtil.genPassword(loginid, password))) {
                                insertAccountLog(loginid, "登录密码错误", "登录", syscode);
                                resMap.put("msg", "账号登录口令错误");
                                resMap.put("code", "0");
                            } else {
                                if (accountSystemList.stream().noneMatch(a -> a.getSysname().equals(syscode))) {
                                    insertAccountLog(loginid, "登录未授权系统", "登录", syscode);
                                    resMap.put("msg", "未授权登录此系统");
                                    resMap.put("code", "0");
                                } else {
                                    insertAccountLog(loginid, "登录成功", "登录", syscode);
                                    resMap.put("code", "20000");
                                    resMap.put("workid", accounts.getWorkid());
                                    resMap.put("name", accounts.getName());
                                }
                            }
                        }
                    }
                }
            }
            return resMap;
        } catch (Exception e) {
            e.printStackTrace();
            resMap.put("msg", "未知错误" + e.getMessage());
            resMap.put("code", "0");
            return resMap;
        }
    }

    public String updatePassword(String loginid, String oldpwd, String newpwd) {
        String result = "0";
        try {
            QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
            accountsQuery.eq("loginid", loginid);
            Accounts accounts = accountsMapper.selectOne(accountsQuery);

            if (accounts == null) {
                result = "未找到该账号";
            } else {
                if (!accounts.getPwd().equals(PasswordUtil.genPassword(loginid, oldpwd))) {
                    insertAccountLog(loginid, "原口令错误", "修改密码", "");
                    result = "账号原口令错误";
                } else {
                    if (!PasswordUtil.iCheckPassword(loginid, newpwd)) {
                        insertAccountLog(loginid, "新口令未达到最低安全标准", "修改密码", "");
                        result = "新口令未达到最低安全标准,或包含工号";
                    } else {
                        accounts.setPwd(PasswordUtil.genPassword(loginid, newpwd));
                        UpdateWrapper<Accounts> accountsUpdate = new UpdateWrapper<>();
                        accountsUpdate.eq("guid", accounts.getGuid());
                        accountsMapper.update(accounts, accountsUpdate);
                        insertAccountLog(loginid, "修改密码成功", "修改密码", "");
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return ("未知错误，请联系管理员 " + e.getMessage());
        }
    }

    public String checkLogin(String loginid, String password, String syscode) {
        String result = "";
        //取系统信息
        QueryWrapper<System> systemQuery = new QueryWrapper<>();
        systemQuery.eq("syscode", syscode);
        System system = systemMapper.selectOne(systemQuery);
        if (system.getStatus().equals("1")) {
            result = "系统维护中,请稍后再试";
        } else if (system.getStatus().equals("2")) {
            result = "系统故障,请稍后再试";
        } else {
            //取账号信息
            QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
            accountsQuery.eq("loginid", loginid);
            Accounts accounts = accountsMapper.selectOne(accountsQuery);

//            //取授权系统
//            QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
//            accountSystemQuery.eq("loginid", loginid);
//            List<AccountSystem> accountSystemList = accountSystemMapper.selectList(accountSystemQuery);

            try {
                if (accounts == null) {
                    insertAccountLog(loginid, "未找到该账号", "登录", syscode);
                    result = "未找到该账号";
                } else {
                    Person person = personMapper.getPerson(accounts.getWorkid());
                    if (person.getAccountLock().equals("1")) {
                        if (person.getStatus().equals("90") || person.getStatus().equals("-90")) {
                            accountsMapper.delete(accountsQuery);
                            //accountSystemMapper.delete(accountSystemQuery);
                        }
                    } else if (person.getAccountLock().equals("0") && (person.getStatus().equals("65") || person.getStatus().equals("68"))) {
                        if (new Date().compareTo(accounts.getEnddate()) > 0) {
                            insertAccountLog(loginid, "账号已过有效期", "登录", syscode);
                            result = "账号已过有效期";
                        } else {
                            if (!accounts.getPwd().equals(PasswordUtil.genPassword(loginid, password))) {
                                insertAccountLog(loginid, "登录密码错误", "登录", syscode);
                                result = "账号登录口令错误";
                            } else {
                                insertAccountLog(loginid, "登录成功", "登录", syscode);
                                result = "0";
//                                if(syscode.equals("portal")){
//                                    insertAccountLog(loginid, "登录成功", "登录", syscode);
//                                    result = "0";
//                                } else {
//                                    if (accountSystemList.stream().noneMatch(a -> a.getSysname().equals(syscode))) {
//                                        insertAccountLog(loginid, "登录未授权系统", "登录", syscode);
//                                        result = "未授权登录此系统";
//                                    } else {
//
//                                    }
//                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertAccountLog(String loginid, String actionContent, String action, String syscode) throws Exception {
        try {
            AccountLog accountLog = new AccountLog();
            accountLog.setLoginid(loginid);
            accountLog.setActionContent(actionContent);
            accountLog.setSyscode(syscode);
            accountLog.setAction(action);
            accountLog.setActionTime(LocalDateTime.now());
            accountLogMapper.insert(accountLog);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAccountToken(String loginid, String token, LocalDateTime expire){
        AccountToken accountToken = new AccountToken();
        accountToken.setToken(token);
        accountToken.setExpire(expire);
        accountToken.setLoginid(loginid);
        accountTokenMapper.insert(accountToken);
    }

    public void delAccountTokenByLoginid(String loginid){
        QueryWrapper<AccountToken> accountToken = new QueryWrapper<>();
        accountToken.eq("loginid",loginid);
        accountTokenMapper.delete(accountToken);
    }

    public void cleanAccountToken(String token){
        QueryWrapper<AccountToken> accountToken = new QueryWrapper<>();
        accountToken.eq("token",token);
        accountTokenMapper.delete(accountToken);
    }

    public AccountSystem authLoginToken(String token, String sysname){
        QueryWrapper<AccountToken> accountTokenQuery = new QueryWrapper<>();
        accountTokenQuery.eq("token",token);
        AccountToken accountToken = accountTokenMapper.selectOne(accountTokenQuery);
        if(accountToken != null && sysname != null){
            QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
            accountSystemQuery.eq("loginid",accountToken.getLoginid());
            accountSystemQuery.eq("sysname",sysname);
            return accountSystemMapper.selectOne(accountSystemQuery);
        }
        return null;
    }

    public Accounts getAccountInfo(String token) throws Exception {
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
            //获取管理中台的权限
            QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
            accountSystemQuery.eq("sysname","acc").eq("loginid",accounts.getLoginid());
            AccountSystem accountSystem = accountSystemMapper.selectOne(accountSystemQuery);
            //取部门岗位信息
            Person person = personMapper.getPerson(accounts.getWorkid());
            //获取权限
            QueryWrapper<Role> roleQuery = new QueryWrapper<>();
            roleQuery.eq("guid",accountSystem.getRole());
            Role role = roleMapper.selectOne(roleQuery);
            //组装账号信息
            accounts.setOrgan(person.getOrgancode());
            accounts.setPost(person.getPost());
            accounts.setPermission(JSON.parseArray(role.getPermission()));
            accounts.setRole(role.getRole());
            return accounts;
        }
        throw new Exception("token过期");
    }

    public String getSystemAccountRole(String loginid,String syscode){
        QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
        accountSystemQuery.eq("loginid",loginid).eq("sysname",syscode);
        return accountSystemMapper.selectOne(accountSystemQuery).getRole();
    }
}
