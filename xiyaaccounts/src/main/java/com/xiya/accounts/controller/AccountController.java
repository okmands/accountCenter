package com.xiya.accounts.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiya.accounts.comm.JwtTokenUtil;
import com.xiya.accounts.comm.PasswordUtil;
import com.xiya.accounts.comm.Result;
import com.xiya.accounts.pojo.Accounts;
import com.xiya.accounts.service.AccountService;
import com.alibaba.fastjson.JSONObject;
import oracle.ucp.proxy.annotation.Post;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

	/**
	 * 账号登录
	 * @param jsonObject
	 * @return java.lang.String
	 * @author okmands
	 * @date 2022/7/16 10:34
	 */
	@PostMapping(value = "/login")
    public String login(@RequestBody JSONObject jsonObject) {
        if (jsonObject.get("syscode") == null || jsonObject.get("loginid") == null || jsonObject.get("password") == null) {
            return Result.toFail("缺少必要参数");
        } else {
            Map<String,Object> result = accountService.login(jsonObject.getString("syscode"), jsonObject.getString("loginid"), jsonObject.getString("password"));
            if (result.get("code").equals("20000")) {
                return Result.toSuccess("登录成功",result);
            } else {
                return Result.toFail(result.get("msg").toString());
            }
        }
    }

    /**
     * 更新账号密码
     * @param jsonObject
     * @return java.lang.String
     * @author okmands
     * @date 2022/7/16 10:33
     */
    @PostMapping(value = "/updatePassword")
    public String updatePassword(@RequestBody JSONObject jsonObject) {
        if (jsonObject.get("loginid") == null || jsonObject.get("oldpwd") == null || jsonObject.get("newpwd") == null) {
            return Result.toFail("缺少必要参数");
        } else {
            String result = accountService.updatePassword(jsonObject.getString("loginid"), jsonObject.getString("oldpwd"), jsonObject.getString("newpwd"));
            if (result.equals("0")) {
                return Result.toSuccess("更新成功");
            }
            return Result.toFail(result);
        }
    }

    @PostMapping("authLoginToken")
    public String authToken(HttpServletRequest request, @RequestBody JSONObject jsonObject){
        try {
            String token = request.getHeader("Authorization");
            String sysname = jsonObject.get("sysname") != null ? jsonObject.getString("sysname") : null;
            return Result.toSuccess("认证成功",accountService.authLoginToken(token,sysname));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping(value = "checkLogin")
    public String checkLogin(@RequestBody JSONObject jsonObject){
        String loginid = jsonObject.getString("loginid");
        String password = jsonObject.getString("password");
        String syscode = jsonObject.getString("syscode");
        if(jsonObject.get("loginid") == null || jsonObject.getString("password") == null || jsonObject.getString("syscode") == null){
            return Result.toFail("缺少必要参数");
        }
        try {
            String result = accountService.checkLogin(loginid,password,syscode);
            if(result.equals("0")){
                HashMap<String,Object> headMap = new HashMap<>();
                HashMap<String,String> claimMap = new HashMap<>();
                claimMap.put("loginid",loginid);
                claimMap.put("password", PasswordUtil.genPassword(loginid,password));
                String signStr = JwtTokenUtil.getSign(headMap,claimMap,8);
                Map<String,Object> map = new HashMap<>();
                map.put("token",signStr);
                //将token写入到数据库
                accountService.delAccountTokenByLoginid(loginid);
                accountService.saveAccountToken(loginid,signStr, LocalDateTime.now().plus(8, ChronoUnit.HOURS));
                return Result.toSuccess("登录成功",map);
            }else{
                return Result.toFail(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

    @PostMapping("getAccountInfo")
    public String getAccountInfo(HttpServletRequest request){
        try {
            return Result.toSuccess(accountService.getAccountInfo(request.getHeader("Authorization")));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toFail(e.getMessage());
        }
    }

///*
// *更新账号信息
// */
//	@RequestMapping(
//			value = "/update",
//			method = {RequestMethod.GET, RequestMethod.POST},
//			produces = {MediaType.APPLICATION_JSON_VALUE}
//			)
//	public String update(
//			@RequestBody(required = false) String body,
//			@RequestParam(value = "loginid", required = false) String loginid,
//			@RequestParam(value = "name", required = false) String name,
//			@RequestParam(value = "status", required = false) String status,
//			@RequestParam(value = "enddate", required = false) String enddate,
//			@RequestParam(value = "callback", required = false) String callback
//			) throws Exception {
//		JSONObject jo_param;
//		if(body == null) {
//			jo_param = new JSONObject()
//				.put("loginid", new URLCodec().decode(loginid))
//				.put("name", new URLCodec().decode(name))
//				.put("status", new URLCodec().decode(status))
//				.put("enddate", new URLCodec().decode(enddate));
//			if(callback != null) {
//				jo_param.put("callback", new URLCodec().decode(callback));
//			}
//		} else {
//			jo_param = new JSONObject(new URLCodec().decode(body));
//		}
//
//		String jsonp_callback = null;
//		if(jo_param.has("callback")) {
//			jsonp_callback = jo_param.getString("callback");
//		}
//
//		JSONObject jo_rst = new JSONObject();
//		if(!jo_param.has("loginid") || !(jo_param.has("name") || jo_param.has("status") || jo_param.has("enddate"))) {
//			jo_rst.put("errcode", -10000).put("errmsg", "缺少请求参数");
//		} else {
//	        try {
//	    		QueryWrapper<Accounts> queryWrapper = new QueryWrapper<>();
//	    		queryWrapper.apply("loginid = '" + jo_param.getString("loginid") + "'");
//	    		List<Accounts> entity = accountsMapper.selectList(queryWrapper);
//
//	    		if(entity.size() == 0) {
//	    			jo_rst.put("errcode", -10001).put("errmsg", "未找到该账号");
//	    		} else {
//	    			Accounts user = entity.get(0);
//	    			Accounts upd_acc = new Accounts();
//	    			if(jo_param.has("name")) {
//	    				upd_acc.setName(jo_param.getString("name"));
//	    			}
//
//	    			if(jo_param.has("enddate")) {
//	    				upd_acc.setEnddate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jo_param.getString("enddate") + " 23:59:59"));
//	    			}
//
//	    			UpdateWrapper<Accounts> updateWrapper = new UpdateWrapper<>();
//					updateWrapper.apply("guid = '" + user.getGuid() + "'");
//					accountsMapper.update(upd_acc, updateWrapper);
//
//					if(jo_param.has("syslist")){
//						JSONArray ja = jo_param.getJSONArray("syslist");
//						for(int i = 0; i < ja.length(); i++) {
//							QueryWrapper<AccountSystem> accountSystemQuery = new QueryWrapper<>();
//							accountSystemQuery.eq("loginid",jo_param.getString("loginid")).eq("sysname",ja.getString(i));
//							accountSystemMapper.delete(accountSystemQuery);
//							AccountSystem accsysentity = new AccountSystem();
//							if(jo_param.getString("status").equals("正常")){
//								accsysentity.setLoginid(jo_param.getString("loginid"));
//								accsysentity.setSysname(ja.getString(i));
//								accountSystemMapper.insert(accsysentity);
//							}
//						}
//					}
//
//					jo_rst.put("errcode", 0).put("errmsg", "ok");
//	    		}
//	        } catch(Exception e) {
//				e.printStackTrace();
//				jo_rst.put("errcode", -20000).put("errmsg", e.getMessage());
//	        }
//		}
//
//		if(jsonp_callback == null) {
//			return jo_rst.toString();
//		} else {
//			return jsonp_callback + "(" + jo_rst + ")";
//		}
//	}
///*
// *重置账号口令
// */
//	@RequestMapping(
//			value = "/forceupdatepassword",
//			method = {RequestMethod.GET, RequestMethod.POST},
//			produces = {MediaType.APPLICATION_JSON_VALUE}
//			)
//	public String forceupdatepassword(
//			@RequestBody(required = false) String body,
//			@RequestParam(value = "loginid", required = false) String loginid,
//			@RequestParam(value = "newpwd", required = false) String newpwd,
//			@RequestParam(value = "callback", required = false) String callback
//			) throws Exception {
//		JSONObject jo_param;
//		if(body == null) {
//			jo_param = new JSONObject()
//				.put("loginid", new URLCodec().decode(loginid))
//				.put("newpwd", new URLCodec().decode(newpwd));
//			if(callback != null) {
//				jo_param.put("callback", new URLCodec().decode(callback));
//			}
//		} else {
//			jo_param = new JSONObject(new URLCodec().decode(body));
//		}
//
//		String jsonp_callback = null;
//		if(jo_param.has("callback")) {
//			jsonp_callback = jo_param.getString("callback");
//		}
//
//		JSONObject jo_rst = new JSONObject();
//		if(!(jo_param.has("loginid") && jo_param.has("newpwd"))) {
//			jo_rst.put("errcode", -10000).put("errmsg", "缺少请求参数");
//		} else {
//	        try {
//	    		QueryWrapper<Accounts> queryWrapper = new QueryWrapper<>();
//	    		queryWrapper.apply("loginid = '" + jo_param.getString("loginid") + "'");
//	    		List<Accounts> entity = accountsMapper.selectList(queryWrapper);
//
//	    		if(entity.size() == 0) {
//	    			jo_rst.put("errcode", -10001).put("errmsg", "未找到该账号");
//	    		} else {
//	    			if(!PasswordUtil.iCheckPassword(jo_param.getString("loginid"), jo_param.getString("newpwd"))) {
//    					jo_rst.put("errcode", -10003).put("errmsg", "新口令未达到最低安全标准");
//    				} else {
//		    			Accounts user = entity.get(0);
//		    			Accounts upd_acc = new Accounts();
//		    			String newpwd1 = PasswordUtil.genPassword(jo_param.getString("loginid"), jo_param.getString("newpwd"));
//	    				upd_acc.setPwd(newpwd1);
//		    			UpdateWrapper<Accounts> updateWrapper = new UpdateWrapper<>();
//						updateWrapper.apply("guid = '" + user.getGuid() + "'");
//						accountsMapper.update(upd_acc, updateWrapper);
//
//						jo_rst.put("errcode", 0).put("errmsg", "ok");
//    				}
//	    		}
//	        } catch(Exception e) {
//				e.printStackTrace();
//				jo_rst.put("errcode", -20000).put("errmsg", e.getMessage());
//	        }
//		}
//
//		if(jsonp_callback == null) {
//			return jo_rst.toString();
//		} else {
//			return jsonp_callback + "(" + jo_rst + ")";
//		}
//	}
//
//	/*
//	 *创建账号
//	 */
//	@RequestMapping(
//		value = "/createuser",
//		method = {RequestMethod.GET, RequestMethod.POST},
//		produces = {MediaType.APPLICATION_JSON_VALUE}
//		)
//	public String createuser(
//			@RequestBody(required = false) String body,
//			@RequestParam(value = "workid", required = false) String workid,
//			@RequestParam(value = "loginid", required = false) String loginid,
//			@RequestParam(value = "name", required = false) String name,
//			@RequestParam(value = "pwd", required = false) String pwd,
//			@RequestParam(value = "enddate", required = false) String enddate,
//			@RequestParam(value = "callback", required = false) String callback
//			) throws Exception {
//		JSONObject jo_param;
//		if(body == null) {
//			jo_param = new JSONObject()
//				.put("workid", new URLCodec().decode(workid))
//				.put("loginid", new URLCodec().decode(loginid))
//				.put("name", new URLCodec().decode(name))
//				.put("pwd", new URLCodec().decode(pwd))
//				.put("enddate", new URLCodec().decode(enddate));
//			if(callback != null) {
//				jo_param.put("callback", new URLCodec().decode(callback));
//			}
//		} else {
//			jo_param = new JSONObject(new URLCodec().decode(body));
//		}
//
//		String jsonp_callback = null;
//		if(jo_param.has("callback")) {
//			jsonp_callback = jo_param.getString("callback");
//		}
//
//		JSONObject jo_rst = new JSONObject();
//		if(!(jo_param.has("workid") && jo_param.has("loginid") && jo_param.has("name") && jo_param.has("pwd") && jo_param.has("enddate"))) {
//			jo_rst.put("errcode", -10000).put("errmsg", "缺少请求参数");
//		} else {
//	        try {
//	        	Accounts accentity = new Accounts();
//	        	accentity.setWorkid(jo_param.getString("workid"));
//	        	accentity.setLoginid(jo_param.getString("loginid"));
//	        	accentity.setName(jo_param.getString("name"));
//	        	accentity.setPwd(PasswordUtil.genPassword(jo_param.getString("loginid"), jo_param.getString("pwd")));
//	        	accentity.setStatus("正常");
//	        	accentity.setEnddate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jo_param.getString("enddate") + " 23:59:59"));
//	        	accountsMapper.insert(accentity);
//
//	        	if(jo_param.has("syslist")){
//					JSONArray ja = jo_param.getJSONArray("syslist");
//					for(int i = 0; i < ja.length(); i++) {
//						AccountSystem accsysentity = new AccountSystem();
//						accsysentity.setLoginid(jo_param.getString("loginid"));
//						accsysentity.setSysname(ja.getString(i));
//						accountSystemMapper.insert(accsysentity);
//					}
//				}
//	        	jo_rst.put("errcode", 0).put("errmsg", "ok");
//	        } catch(Exception e) {
//				jo_rst.put("errcode", -20000).put("errmsg", e.getMessage());
//				log.error(e.getMessage());
//	        }
//		}
//
//		if(jsonp_callback == null) {
//			return jo_rst.toString();
//		} else {
//			return jsonp_callback + "(" + jo_rst + ")";
//		}
//	}
//
//	/*
//	查询一个账号
//	 */
//	@RequestMapping("selectuser")
//	public String selectUser(
//			@RequestBody(required = false) String body,
//			@RequestParam(value = "workid", required = false) String workid,
//			@RequestParam(value = "callback", required = false) String callback
//	) throws Exception{
//		JSONObject jo_param;
//		if(body == null) {
//			jo_param = new JSONObject().put("workid", new URLCodec().decode(workid));
//			if(callback != null) {
//				jo_param.put("callback", new URLCodec().decode(callback));
//			}
//		} else {
//			jo_param = new JSONObject(new URLCodec().decode(body));
//		}
//
//		String jsonp_callback = null;
//		if(jo_param.has("callback")) {
//			jsonp_callback = jo_param.getString("callback");
//		}
//
//		JSONObject jo_rst = new JSONObject();
//		if(!(jo_param.has("workid"))) {
//			jo_rst.put("errcode", -10000).put("errmsg", "缺少请求参数");
//		} else {
//			try {
//				QueryWrapper<Accounts> accountsQuery = new QueryWrapper<>();
//				accountsQuery.eq("workid",jo_param.getString("workid"));
//				Accounts accounts = accountsMapper.selectOne(accountsQuery);
//				if(accounts != null){
//					if(!accounts.getStatus().equals("正常")){
//						jo_rst.put("errcode", -10002).put("errmsg", "账号禁用");
//					}else{
//						jo_rst.put("errcode", 0).put("errmsg", "ok");
//					}
//				}else{
//					jo_rst.put("errcode", -10001).put("errmsg", "账号不存在");
//				}
//			} catch(Exception e) {
//				jo_rst.put("errcode", -20000).put("errmsg", e.getMessage());
//				log.error(e.getMessage());
//			}
//		}
//
//		if(jsonp_callback == null) {
//			return jo_rst.toString();
//		} else {
//			return jsonp_callback + "(" + jo_rst + ")";
//		}
//	}
}

