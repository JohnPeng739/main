package com.ds.retl.rest.error;

import org.mx.rest.error.UserInterfaceError;

/**
 * 人机界面错误枚举，定义了可以反馈到操作界面上的错误信息。
 *
 * @author : john.peng created on date : 2017/10/6
 * @see UserInterfaceError
 */
public enum UserInterfaceErrors implements UserInterfaceError {
    SYSTEM_ILLEGAL_PARAM(1, "输入的参数错误，请联系开发人员。"),
    SYSTEM_UNSUPPORTED_OPERATE(2, "不支持的操作方法，请联系开发人员。"),
    SYSTEM_FILE_OPERATE_FAIL(3, "文件操作失败。"),

    DB_OPERATE_FAIL(51, "数据库操作失败。"),

    USER_NOT_FOUND(101, "指定的用户不存在。"),
    USER_PASSWORD_UNMATCH(102, "输入的密码不正确。"),
    USER_PASSWORD_DISGEST_FAIL(103, "密码加密处理失败。"),
    USER_SAVE_LOGIN_FAIL(104, "写入登录信息失败。"),
    USER_SAVE_LOGOUT_FAIL(105, "写入登出信息失败。"),

    TOPOLOGY_NOT_FOUND(301, "指定的计算拓扑不存在。"),
    TOPOLOGY_VALIDATE_FAIL(302, "校验计算拓扑所使用的资源发生错误。"),
    TOPOLOGY_CONF_JDBC_SPOUT(303, "JDBC类型的采集源配置错误，同一个拓扑中只能且只能配置一个JDBC采集源。"),

    OTHER(9999, "系统未识别的错误。");

    private int errorCode;
    private String errorMessage;

    /**
     * 构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误信息
     */
    private UserInterfaceErrors(int errorCode, String errorMessage) {
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
