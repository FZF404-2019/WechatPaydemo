package com.liangchan.wechatpaydemo.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回实体类
 */
@Data
public class ResultBean<T> implements Serializable {

    public static final Boolean SUCCESS = Boolean.TRUE;

    public static final Boolean FAIL = Boolean.FALSE;

    /**
     * 请求响应码
     */
    private Integer code = 0;

    /**
     * 请求信息
     */
    private String message = "请求成功";

    /**
     * 判断请求是否成功
     */
    private Boolean success = SUCCESS;

    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(ResultEnum resultEnum){
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.success = resultEnum.getCode()==0;
    }

    public ResultBean(int code,String message) {
        this.code = code;
        this.message = message;
        this.success = code == 0;
    }

    public ResultBean(ResultEnum resultEnum, T data){
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.success = resultEnum.getCode()==0;
        this.data = data;
    }

    public ResultBean(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.success = code == 0;
        this.data = data;
    }

}
