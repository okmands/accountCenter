package com.xiya.accounts.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("account_system")
public class AccountSystem {
	@TableId(type = IdType.AUTO)
	String loginid;
	String sysname;
	String role;
	String roleName;
}
