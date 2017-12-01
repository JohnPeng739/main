package org.mx.comps.rbac.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 人机界面错误异常类
 *
 * @author : john.peng created on date : 2017/10/8
 */
public class UserInterfaceRbacErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    public UserInterfaceRbacErrorException() {
        this(RbacErrors.RBAC_OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceRbacErrorException(RbacErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * RBAC(基于角色的访问控制)操作错误枚举定义
     */
    public enum RbacErrors implements UserInterfaceError {
        USER_NOT_FOUND( "指定的用户不存在。"),

        ACCOUNT_NOT_FOUND("指定的账户不存在。"),
        ACCOUNT_PASSWORD_NOT_MATCHED("输入的账户密码不正确。"),
        ACCOUNT_DIGEST_PASSWORD_FAIL("加密账户密码失败。"),
        ACCOUNT_ALREADY_LOGINED("指定的账户已经登录，请不要重复登录。"),
        ACCOUNT_NOT_LOGIN("账户尚未登录。"),
        ACCOUNT_HAS_EXIST("指定的账户已经存在。"),

        ROLE_NOT_FOUND("指定的角色不存在。"),

        PRIVILEGE_NOT_FOUND("指定的特权不存在。"),

        DEPARTMENT_NOT_FOUND("指定的部门不存在。"),

        ACCREDIT_NOT_FOUND("指定的授权不存在。"),
        ACCREDIT_SAME_FOUND("指定的授权已经存在，不能重复创建。"),
        ACCREDIT_HAS_CLOSED("指定的收取已经被关闭，不能重复关闭。"),

        RBAC_OTHER_FAIL("未知基于角色的访问控制错误。");

        public static final int BASE_ORDINAL = 100;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        private RbacErrors(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        /**
         * {@inheritDoc}
         *
         * @see UserInterfaceError#getErrorCode()
         */
        @Override
        public int getErrorCode() {
            return BASE_ORDINAL + super.ordinal();
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
}
