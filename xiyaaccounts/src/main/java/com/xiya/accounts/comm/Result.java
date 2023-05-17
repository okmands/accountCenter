package com.xiya.accounts.comm;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;


public class Result implements Serializable {

    private int code;

    private String msg;

    private Object data;


    public static Result fromJson(String json) {
        Result error = JSON.parseObject(json, Result.class);
        return error;
    }
    public static String toFail(String msg){
        return to(1,msg);
    }

    public static String toFail(int code,String msg){
        return to(code,msg);
    }

    public static String toSuccess(String msg){
        return to(20000,msg);
    }
    public static String toSuccess(Object data){
        return to("",data);
    }
    public static String toSuccess(String msg,Object data){
        return to(msg,data);
    }

    //public static String toSuccess(String msg,Object data){ return toWithNoLower(msg,data); }

    private static String to(int code,String msg){
        Result result = new Result();
        result.setMsg(msg);
        result.setData("");
        result.setCode(code);
        return JSON.toJSONString(result,SerializerFeature.DisableCircularReferenceDetect);
    }

    private static String to(String msg,Object data){
        Result result = new Result();
        result.setMsg(msg);
        result.setData(data);
        result.setCode(20000);
        return JSON.toJSONString(result,SerializerFeature.DisableCircularReferenceDetect);
    }

    private static String toWithNoLower(String msg,Object data){
        Result result = new Result();
        result.setMsg(msg);
        result.setData(data);
        result.setCode(0);
        return JSON.toJSONString(result,SerializerFeature.DisableCircularReferenceDetect);
    }

    public String toString(){
        return JSON.toJSONString(this);
    }



    public int getCode() {
        return code;
    }

    public void setCode(int errcode) {
        this.code = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String errmsg) {
        this.msg = errmsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

