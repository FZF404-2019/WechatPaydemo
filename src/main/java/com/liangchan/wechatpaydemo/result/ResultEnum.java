package com.liangchan.wechatpaydemo.result;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 返回结果响应码
 */
@Getter
public enum ResultEnum {

    SUCCESS(0,"请求成功"),

    NOT_FOUND(404,"接口不存在"),

    /**
     * 系统程序相关异常
     */
    SYSTEM_ERROR(500,"系统内部异常"),
    SQL_ERROR(501,"DB执行异常"),
    REDIS_ERROR(502,"Redis异常"),
    HTTP_ERROR(503,"HTTP请求出错"),
    INTERFACE_ERROR(504,"接口定义异常"),
    OTHER_API_ERROR(505,"调用第三方平台API异常"),

    /**
     * 700-799 拦截请求
     */
    PARAM_ERROR(700,"参数异常"),

    ;

    private final Integer code;

    @JsonValue
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
