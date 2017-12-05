package org.mx.service.rest.vo;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * REST返回的基础数据值对象
 *
 * @author : john.peng date : 2017/10/6
 */
public class DataVO<T> {
    private int errorCode;
    private String errorMessage;
    private T data;

    /**
     * 默认的构造函数
     */
    public DataVO() {
        this.errorCode = 0;
        this.errorMessage = null;
    }

    /**
     * 默认的构造函数
     *
     * @param data 数据对象
     */
    public DataVO(T data) {
        this.errorCode = 0;
        this.errorMessage = null;
        this.errorCode = 0;
        this.errorMessage = null;
        this.data = data;
    }

    /**
     * 默认的构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public DataVO(int errorCode, String errorMessage) {
        this.errorCode = 0;
        this.errorMessage = null;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 默认的构造函数
     *
     * @param error 人机界面错误
     */
    public DataVO(UserInterfaceError error) {
        this(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * 默认的构造函数
     *
     * @param exception 人机界面异常
     */
    public DataVO(UserInterfaceException exception) {
        this(exception.getErrorCode(), exception.getErrorMessage());
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * 设置错误代码
     *
     * @param errorCode 错误代码
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * 设置错误消息
     *
     * @param errorMessage 错误消息
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 获取数据对象
     *
     * @return 数据对象
     */
    public T getData() {
        return this.data;
    }

    /**
     * 设置数据对象
     *
     * @param data 数据对象
     */
    public void setData(T data) {
        this.data = data;
    }
}
