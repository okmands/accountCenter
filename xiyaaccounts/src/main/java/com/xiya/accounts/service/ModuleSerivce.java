package com.xiya.accounts.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiya.accounts.mapper.account.ModuleMapper;
import com.xiya.accounts.pojo.Module;
import org.bouncycastle.math.raw.Mod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleSerivce {

    @Resource
    private ModuleMapper moduleMapper;

    public PageInfo<Module> getModule(int current , int pageSize){
        QueryWrapper<Module> moduleQuery = new QueryWrapper<>();
        moduleQuery.orderByAsc("\"order\"");
        PageHelper.startPage(current,pageSize);
        PageInfo<Module> modulePageInfo = new PageInfo<>(moduleMapper.selectList(moduleQuery));
        List<Module> tempList = modulePageInfo.getList();
        List<Module> moduleList = tempList.stream().filter(s->s.getPid() == null).collect(Collectors.toList());
        for (Module module : moduleList){
            List<Module> childrenList = getChildrenModule(tempList,module.getGuid());
            if(childrenList.stream().count() > 0){
                module.setChildren(childrenList);
            }
        }
        modulePageInfo.setList(moduleList);
        return modulePageInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addModule(Module module) throws Exception {
        try {
            moduleMapper.insert(module);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateModule(Module module) throws Exception {
        try {
            moduleMapper.updateById(module);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(List<String> ids) throws Exception {
        try {
            moduleMapper.deleteBatchIds(ids);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
        }
    }

    public List<Module> getChildrenModule(List<Module> tempList,String guid){
        List<Module> moduleList = tempList.stream().filter(s->s.getPid() != null && s.getPid().equals(guid)).collect(Collectors.toList());
        for (Module module : moduleList){
            List<Module> childrenList = getChildrenModule(tempList,module.getGuid());
            if(childrenList.stream().count() > 0){
                module.setChildren(childrenList);
            }
        }
        return moduleList;
    }
}
