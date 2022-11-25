package com.liangchan.wechatpaydemo.exception;


import com.liangchan.wechatpaydemo.result.ResultEnum;

/**
 * Created by IntelliJ IDEA.
 *
 * @Project: WechatPaydemo
 * @author: liangchan
 * @DateTime: Created in 2022/5/17 11:56
 * @description: 业务提示性异常
 */
public class ServiceBusinessException extends ServiceException{

    public ServiceBusinessException(ResultEnum resultEnum) {
        super(resultEnum);
    }

    public ServiceBusinessException(ResultEnum resultEnum, String msg) {
        super(resultEnum, msg);
    }

    public ServiceBusinessException(String detail, ResultEnum resultEnum) {
        super(detail, resultEnum);
    }
}
