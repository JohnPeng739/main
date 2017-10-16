package org.mx.dal.exception;

/**
 * 实体实例化异常定义
 *
 * @author : john.peng date : 2017/8/18
 */
public class EntityInstantiationException extends Exception {
    /**
     * 默认的构造函数
     *
     * @param message 异常内容
     * @param cause   异常原因
     */
    public EntityInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
