package com.xiya.accounts.pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("system_list")
public class System implements Serializable {
    @TableId(type = IdType.AUTO)
    private String id;
    private String syscode;
    private String sysname;
    private String loginUrl;
    private String status;
    private String domain;
    private String serverIp;
    @TableField(exist = false)
    private int usercount;
    private String roleApi;
    private String apiParameter;
    @TableField(exist = false)
    private JSONArray role;
}
