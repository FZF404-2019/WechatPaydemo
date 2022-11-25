package com.liangchan.wechatpaydemo.core.handler;

import cn.hutool.core.util.StrUtil;
import com.liangchan.wechatpaydemo.exception.ServiceBusinessException;
import com.liangchan.wechatpaydemo.exception.ServiceException;
import com.liangchan.wechatpaydemo.result.ResultBean;
import com.liangchan.wechatpaydemo.result.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param e 异常对象
     * @return 响应对象
     */
    @ExceptionHandler(value = ServiceException.class)
    public ResultBean<?> scudErrorHandler(ServiceException e) {
        log.error(e.getMessage(), e);
        ResultBean<String> result = new ResultBean<>();
        result.setCode(e.getCode());
        result.setSuccess(ResultBean.FAIL);
        result.setMessage(e.getMessage());
        result.setData(e.getData());
        return result;
    }

    /**
     * 捕获业务异常
     * @param e 异常对象
     * @return 响应对象
     */
    @ExceptionHandler(value = ServiceBusinessException.class)
    public ResultBean<?> scudBusinessHandler(ServiceException e) {
        log.warn(e.getMessage());
        ResultBean<String> result = new ResultBean<>();
        result.setCode(e.getCode());
        result.setMessage(e.getMessage());
        result.setSuccess(ResultBean.FAIL);
        result.setData(e.getData());
        return result;
    }

    /**
     * 参数校验异常（接口中使用了 @RequestBody 时进入该处理方法）
     *
     * @param e 异常对象
     * @return ResultBean
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultBean<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        if (e.getBindingResult().hasErrors()) {
            return createParamErrorResultBean(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        }
        return createParamErrorResultBean(e.getMessage());
    }

    /**
     * 参数校验异常（接口中不使用 @RequestBody 时进入该处理方法）
     *
     * @param e 异常对象
     * @return ResultBean
     */
    @ExceptionHandler(value = BindException.class)
    public ResultBean<?> handlerBindException(BindException e) {
        log.error(e.getMessage());
        if (e.getBindingResult().hasErrors()) {
            return createParamErrorResultBean(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        }
        return createResultBean(ResultEnum.PARAM_ERROR,e.getMessage());
    }


    /**
     * 捕获SQL异常
     * @param e 异常对象
     * @return 响应对象
     */
    @ExceptionHandler(value = SQLException.class)
    public ResultBean<?> sqlErrorHandler(SQLException e) {
        log.error("SQL异常：{}",e.getMessage());
        return new ResultBean<>(ResultEnum.SQL_ERROR);
    }


    /**
     * 总异常处理
     *
     * @param e 异常对象
     * @return 错误信息
     */
    @ExceptionHandler(value = Exception.class)
    public ResultBean<?> errorHandler(Exception e) {
        log.error(e.getMessage(), e);
        return new ResultBean<>(ResultEnum.SYSTEM_ERROR);
    }

    private ResultBean<?> createParamErrorResultBean(String message) {
        ResultBean<?> result = new ResultBean<>();
        result.setCode(ResultEnum.PARAM_ERROR.getCode());
        result.setMessage(message);
        result.setSuccess(ResultBean.FAIL);
        return result;
    }

    private ResultBean<?> createResultBean(ResultEnum resultEnum, String message) {
        ResultBean<?> result = new ResultBean<>();
        result.setCode(resultEnum.getCode());
        if(StrUtil.isNotBlank(message)){
            result.setMessage(message);
        } else {
            result.setMessage(resultEnum.getMessage());
        }
        result.setSuccess(ResultBean.FAIL);
        return result;
    }
}
