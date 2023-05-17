package com.xiya.accounts.pojo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName("accounts")
public class Accounts {
	@TableId(type = IdType.AUTO)
	String guid;
	String workid;
	String loginid;
	String name;
	String pwd;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JSONField(format = "yyyy-MM-dd")
	Date enddate;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdate;
	@TableField(exist = false)
	String organ;
	@TableField(exist = false)
	String post;
	@TableField(exist = false)
	String status;
	@TableField(exist = false)
	List<Map<String,Object>> authSystemList;
	@TableField(exist = false)
	private String role;
	@TableField(exist = false)
	private JSONArray permission;
}
