package com.liangchan.wechatpaydemo.exception;

import com.liangchan.wechatpaydemo.result.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目相关异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {

    private Integer code;
    private String message;
    private String data;

    private ResultEnum resultEnum;

    /**
     * 创建异常
     * @param resultEnum 异常枚举
     */
    public ServiceException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.message=resultEnum.getMessage();
        this.code = resultEnum.getCode();
        this.resultEnum = resultEnum;
    }

    /**
     * 创建异常
     * @param resultEnum 异常枚举
     * @param msg 显示的异常信息
     */
    public ServiceException(ResultEnum resultEnum, String msg) {
        super(msg);
        this.message=msg;
        this.code = resultEnum.getCode();
        this.resultEnum = resultEnum;
        this.data = resultEnum.getMessage();
    }

    /**
     * 创建异常
     * @param detail 异常描述
     * @param resultEnum 异常枚举【显示的信息为枚举值】
     */
    public ServiceException(String detail, ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.message=resultEnum.getMessage();
        this.code = resultEnum.getCode();
        this.resultEnum = resultEnum;
        this.data = detail;
    }


}
