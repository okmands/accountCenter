package com.xiya.accounts.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.mapper.account.ModuleMapper;
import com.xiya.accounts.mapper.account.RoleMapper;
import com.xiya.accounts.mapper.account.SystemMapper;
import com.xiya.accounts.mapper.hr.OrganMapper;
import com.xiya.accounts.pojo.Module;
import com.xiya.accounts.pojo.Organ;
import com.xiya.accounts.pojo.Role;
import com.xiya.accounts.pojo.System;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private OrganMapper organMapper;
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private SystemMapper systemMapper;

    public PageInfo<Role> getRole(Integer current, Integer pageSize, String search, JSONArray role){
        QueryWrapper<Role> roleQuery = new QueryWrapper<>();
        roleQuery.and(a->a.like("role_name",search));
        if(role != null && role.size() > 0){
            roleQuery.in("role",role);
        }
        PageHelper.startPage(current,pageSize);
        List<Role> roleList = roleMapper.selectList(roleQuery);
        return new PageInfo<>(roleList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRole(Role role) throws Exception {
        try {
            roleMapper.insert(role);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role) throws Exception {
        try {
            roleMapper.updateById(role);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(List<String> ids) throws Exception {
        try {
            roleMapper.deleteBatchIds(ids);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    public List<Organ> getOrgan(){
        QueryWrapper<Organ> organQuery = new QueryWrapper<>();
        organQuery.eq("enable","1");
        organQuery.isNull("pid");
        organQuery.orderByAsc("oldnum");
        return organMapper.selectList(organQuery);
    }

//    public List<Organ> getChildrenOrgan(List<Organ> tempList,String id){
//        List<Organ> organList = tempList.stream().filter(s->s.getPid() != null && s.getPid().equals(id)).collect(Collectors.toList());
//        for (Organ organ : organList){
//            List<Organ> childrenList = getChildrenOrgan(tempList,organ.getId());
//            if(childrenList.size() > 0){
//                organ.setChildren(childrenList);
//            }else{
//                organ.setIsLeaf(true);
//            }
//        }
//        return organList;
//    }

    public List<Module> getModule(){
        QueryWrapper<Module> moduleQuery = new QueryWrapper<>();
        moduleQuery.orderByAsc("\"order\"");
        List<Module> tempList = moduleMapper.selectList(moduleQuery);
        List<Module> moduleList = tempList.stream().filter(s->s.getPid() == null).collect(Collectors.toList());
        for (Module module : moduleList){
            List<Module> childrenList = getChildrenModule(tempList,module.getGuid());
            if(childrenList.size() > 0){
                module.setChildren(childrenList);
            }else{
                module.setIsLeaf(true);
            }
        }
        return moduleList;
    }

    public List<Module> getChildrenModule(List<Module> tempList, String guid){
        List<Module> moduleList = tempList.stream().filter(s->s.getPid() != null && s.getPid().equals(guid)).collect(Collectors.toList());
        for (Module module : moduleList){
            List<Module> childrenList = getChildrenModule(tempList,module.getGuid());
            if(childrenList.size() > 0){
                module.setChildren(childrenList);
            }else{
                module.setIsLeaf(true);
            }
        }
        return moduleList;
    }

    public List<System> getSystem() {
        QueryWrapper<System> systemQuery = new QueryWrapper<>();
        systemQuery.eq("status","0");
        systemQuery.orderByAsc("syscode");
        return systemMapper.selectList(systemQuery);
    }

    public String getRoleList(){
        QueryWrapper<Role> roleQuery = new QueryWrapper<>();
        roleQuery.select("role_name text,guid id");
        List<Role> roleList = roleMapper.selectList(roleQuery);
        for (Role role : roleList) {
            role.setIsLeaf(true);
        }
        return JSONObject.toJSONString(roleList);
    }
}
