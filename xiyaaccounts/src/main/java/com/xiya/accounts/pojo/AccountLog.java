package com.xiya.accounts.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("account_log")
public class AccountLog {
    @TableId(type = IdType.AUTO)
    public String id;
    public String loginid;
    public String syscode;
    public String action;
    public String actionContent;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    public LocalDateTime actionTime;
}
