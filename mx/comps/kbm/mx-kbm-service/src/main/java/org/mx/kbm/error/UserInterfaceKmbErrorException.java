package org.mx.kbm.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 描述： KBM人机交互错误异常类
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午9:35
 */
public class UserInterfaceKmbErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    public UserInterfaceKmbErrorException() {
        this(KmbErrors.KMB_OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceKmbErrorException(KmbErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * RBAC(基于角色的访问控制)操作错误枚举定义
     */
    public enum KmbErrors implements UserInterfaceError {
        TENANT_NOT_FOUND("指定的租户不存在。"),
        TENANT_HAS_EXISTED("指定的租户已经存在。"),

        CATEGORY_NOT_FOUND("指定的分类目录不存在。"),
        CATEGORY_HAS_EXISTED("指定的分类目录已经存在"),

        KTAG_NOT_FOUND("指定的标签不存在。"),
        KTAG_HAS_EXISTED("指定标签已经存在。"),

        KMB_OTHER_FAIL("未知知识库操作错误。");

        public static final int BASE_ORDINAL = 1000;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        private KmbErrors(String errorMessage) {
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
