package com.ds.retl.exception;

import com.ds.retl.rest.error.UserInterfaceErrors;
import org.mx.rest.error.UserInterfaceException;

/**
 * 人机界面错误异常类
 *
 * @author : john.peng created on date : 2017/10/8
 */
public class UserInterfaceErrorException extends UserInterfaceException {
    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceErrorException(UserInterfaceErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }
}
