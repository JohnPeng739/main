package org.mx.tools.ffee.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 描述： FFEE人机交互错误异常定义
 *
 * @author John.Peng
 *         Date time 2018/2/19 下午5:18
 */
public class UserInterfaceFfeeErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    UserInterfaceFfeeErrorException() {
        this(UserInterfaceFfeeErrorException.FfeeErrors.FFEE_OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * RBAC(基于角色的访问控制)操作错误枚举定义
     */
    public enum FfeeErrors implements UserInterfaceError {
        FAMILY_NOT_EXISTED("指定的家庭不存在。"),
        FAMILY_EXISTED("指定的家庭已经存在，不能创建重名的家庭。"),

        ACCOUNT_NOT_EXISTED("指定的账户不存在。"),
        ACCOUNT_IN_MEMBERS("指定的账户已经是家庭成员。"),

        COURSE_NOT_EXISTED("指定的科目不存在。"),

        FAMILY_MEMBER_SAVE_FAIL("保存家庭成员信息失败。"),

        FFEE_OTHER_FAIL("未知家庭账簿错误。");

        public static final int BASE_ORDINAL = 100;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        FfeeErrors(String errorMessage) {
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