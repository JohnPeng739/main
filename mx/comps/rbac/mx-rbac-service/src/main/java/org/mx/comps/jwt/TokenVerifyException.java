package org.mx.comps.jwt;

/**
 * 描述： 用户令牌验证失败异常类。
 *
 * @author John.Peng
 *         Date time 2018/3/28 上午9:05
 */
public class TokenVerifyException extends Exception {
    private static final String defaultMessage = "Token verify exception.";

    public TokenVerifyException() {
        super(defaultMessage);
    }

    public TokenVerifyException(String message) {
        super(message);
    }

    public TokenVerifyException(Throwable ex) {
        super(defaultMessage, ex);
    }

    public TokenVerifyException(String message, Throwable ex) {
        super(message, ex);
    }
}
