package org.mx.dal.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 人机界面错误异常类
 *
 * @author : john.peng created on date : 2017/10/8
 */
public class UserInterfaceDalErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    public UserInterfaceDalErrorException() {
        this(DalErrors.DB_OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceDalErrorException(DalErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * DAL操作错误枚举定义
     */
    public enum DalErrors implements UserInterfaceError {
        DB_OPERATE_FAIL("数据库操作失败。"),
        ENTITY_INSTANCE_FAIL("创建指定的实体失败。"),
        ENTITY_NOT_FOUND("指定的实体不存在。"),
        ENTITY_INVALID_BASE("指定的实体没有实现Base接口。"),

        ENTITY_INDEX_INVALID_BASE("指定的实体没有继承ElasticBaseEntity。"),
        ENTITY_INDEX_NOT_FOUND("指定实体没有建立索引。"),

        ES_INDEX_FAIL("指定实体的索引操作失败。"),

        DB_OTHER_FAIL("未知数据访问错误。");

        public static final int BASE_ORDINAL = 50;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        private DalErrors(String errorMessage) {
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
