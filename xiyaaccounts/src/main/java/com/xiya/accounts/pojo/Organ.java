package com.xiya.accounts.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("comm.organ")
@Repository
public class Organ implements Serializable {
    @TableId(type = IdType.AUTO)
    private String id;
    private String oldnum;
    private String cname;
    private String lname;
    private String pid;
    private String enable;
    @TableField(exist = false)
    private List<Organ> children;
    @TableField(exist = false)
    private boolean isLeaf;
    public boolean getIsLeaf(){
        return isLeaf;
    }
    public void setIsLeaf(boolean leaf){
       this.isLeaf = leaf;
    }
}
