package org.mx.error;

/**
 * 人机界面异常定义
 *
 * @author : john.peng date : 2017/10/6
 * @see UserInterfaceError
 */
public class UserInterfaceException extends RuntimeException implements UserInterfaceError {
    private int errorCode;
    private String errorMessage;

    /**
     * 默认的构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public UserInterfaceException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 默认的构造函数
     *
     * @param errorCode        错误代码
     * @param errorMessage     错误消息
     * @param exceptionMessage 异常内容
     */
    public UserInterfaceException(int errorCode, String errorMessage, String exceptionMessage) {
        super(exceptionMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 默认的构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @param throwable    异常原因
     */
    public UserInterfaceException(int errorCode, String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 默认的构造函数
     *
     * @param errorCode        错误代码
     * @param errorMessage     错误消息
     * @param exceptionMessage 异常消息
     * @param throwable        异常原因
     */
    public UserInterfaceException(int errorCode, String errorMessage, String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * {@inheritDoc}
     *
     * @see UserInterfaceError#getErrorCode()
     */
    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * {@inheritDoc}
     *
     * @see UserInterfaceError#getErrorMessage()
     */
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}

