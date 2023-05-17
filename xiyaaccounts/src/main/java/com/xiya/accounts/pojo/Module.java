package com.xiya.accounts.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("module")
public class Module implements Serializable {
    @TableId(type = IdType.AUTO)
    private String guid;
    private String pid;
    private String title;
    private String name;
    private String link;
    @TableField("\"order\"")
    private String order;
    @TableField(exist = false)
    private List<Module> children;
    @TableField(exist = false)
    private boolean isLeaf;
    public boolean getIsLeaf(){
        return isLeaf;
    }
    public void setIsLeaf(boolean leaf){
        this.isLeaf = leaf;
    }
}
