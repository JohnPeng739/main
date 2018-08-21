package org.mx.jwt.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 描述： JWT(JSON Web Tokens)错误枚举定义
 *
 * @author john peng
 * Date time 2018/8/19 下午7:51
 */
public class UserInterfaceJwtErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    public UserInterfaceJwtErrorException() {
        this(JwtErrors.JWT_OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceJwtErrorException(JwtErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * JWT（JSON Web Tokens）操作错误枚举定义
     */
    public enum JwtErrors implements UserInterfaceError {
        BLANK_TOKEN("JWT令牌为空。"),
        UNSUPPORTED_ALGORITHM("指定的JWT算法不存在。"),
        NOT_AUTHENTICATED("没有通过身份认证。"),
        JWT_NOT_INITIALIZE("JWT尚未正确初始化。"),
        JWT_INITIALIZE_FAIL("初始化JWT失败。"),
        JWT_SIGN_FAIL("签发JWT令牌失败。"),
        JWT_VERIFY_FAIL("验证JWT令牌失败。"),

        JWT_OTHER_FAIL("未知JWT(JSON Web Tokens)错误。");

        public static final int BASE_ORDINAL = 130;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        JwtErrors(String errorMessage) {
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
