package com.atguigu.utils.utils;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

//统一返回结果的类
@Data
public class BaseResponse {

    private Boolean success;

    private Integer code;

    private String message;

    private Map<String, Object> data = new HashMap<String, Object>();

    //把构造方法私有
    private BaseResponse() {}

    //成功静态方法
    public static BaseResponse ok() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);
        baseResponse.setCode(20000);
        baseResponse.setMessage("成功");
        return baseResponse;
    }

    //失败静态方法
    public static BaseResponse error() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(false);
        baseResponse.setCode(20001);
        baseResponse.setMessage("失败");
        return baseResponse;
    }

    public BaseResponse success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public BaseResponse message(String message){
        this.setMessage(message);
        return this;
    }

    public BaseResponse code(Integer code){
        this.setCode(code);
        return this;
    }

    public BaseResponse data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public BaseResponse data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
