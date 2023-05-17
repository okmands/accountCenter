package com.xiya.accounts.pojo;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("role")
public class Role implements Serializable {
    @TableId(type = IdType.AUTO)
    private String guid;
    private String permission;
    private String organ;
    @TableField("\"system\"")
    private String system;
    private String roleName;
    private String role;
    @TableField(exist = false)
    private String text;
    @TableField(exist = false)
    private String id;
    @TableField(exist = false)
    private boolean isLeaf;
    public boolean getIsLeaf(){
        return isLeaf;
    }
    public void setIsLeaf(boolean leaf){
        this.isLeaf = leaf;
    }
}
