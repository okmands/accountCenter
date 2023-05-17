package com.xiya.accounts.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Data
@TableName("hr.person")
@Repository
public class Person implements Serializable {
    @TableId(type = IdType.AUTO)
    private String guid;
    private String gender;
    private String organcode;
    private String post;
    private String sname;
    private String jobnum;
    private String sfid;
    private String phone;
    private String status;
    private String accountLock;
}
