package org.mx.comps.rbac.error;

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

    DB_OPERATE_FAIL(51, "数据库操作失败。"),

    ENTITY_INSTANCE_FAIL(71, "创建实体失败。"),

    USER_NOT_FOUND(101, "指定的用户不存在。"),

    ACCOUNT_NOT_FOUND(131, "指定的账户不存在。"),
    ACCOUNT_PASSWORD_NOT_MATCHED(132, "输入的账户密码不正确。"),
    ACCOUNT_DIGEST_PASSWORD_FAIL(133, "加密账户密码失败。"),
    ACCOUNT_ALREADY_LOGINED(134, "指定的账户已经登录，请不要重复登录。"),
    ACCOUNT_NOT_LOGIN(135, "账户尚未登录。"),

    ROLE_NOT_FOUND(151, "指定的角色不存在。"),

    PRIVILEGE_NOT_FOUND(171, "指定的特权不存在。"),

    DEPARTMENT_NOT_FOUND(191, "指定的部门不存在。"),

    ACCREDIT_NOT_FOUND(201, "指定的授权不存在。"),

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
