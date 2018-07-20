package org.mx.hanlp.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 系统级错误异常
 *
 * @author : john.peng created on date : 2017.11.29
 */
public class UserInterfaceHanlpErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    public UserInterfaceHanlpErrorException() {
        this(HanlpErrors.OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceHanlpErrorException(HanlpErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * 系统级错误信息枚举定义
     */
    public enum HanlpErrors implements UserInterfaceError {
        SUGGESTER_NOT_FOUND("指定的推荐器不存在，请联系开发人员。"),
        SUGGESTER_UNMATCH("指定的推荐器不匹配，请联系开发人员。"),

        DB_NO_DRIVER("没有配置指定的数据库驱动程序，请联系开发人员。"),
        DB_NO_URL("没有配置数据库访问字符串，请联系开发人员。"),
        DB_CONNECT_FAIL("连接数据库失败。"),
        DB_MAPPING_ERROR("推荐数据字段映射配置错误。"),

        DATA_INVALIDATION("推荐数据非法。"),

        OTHER_FAIL("未知的系统错误。");

        public static final int BASE_ORDINAL = 150;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        private HanlpErrors(String errorMessage) {
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
