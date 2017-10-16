package org.mx.dal.exception;

/**
 * 实体操作异常定义
 *
 * @author : john.peng date : 2017/8/18
 */
public class EntityAccessException extends Exception {
    /**
     * 默认的构造函数
     *
     * @param message 异常内容
     */
    public EntityAccessException(String message) {
        super(message);
    }

    /**
     * 默认的构造函数
     *
     * @param message 异常内容
     * @param cause   异常原因
     */
    public EntityAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
