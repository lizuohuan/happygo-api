package com.magicbeans.happygo.config;

import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.exception.BusinessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常拦截处理
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {


    /**
     * 拦截业务异常进行处理并返回标准格式
     * @param exception
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseData businessException(BusinessException exception) {
        ResponseData result = new ResponseData();
        result.setError(exception.getErrorCode(),exception.getMessage());
        return result;
    }



}
