package org.mx.dal.exception;

/**
 * 指定实体不存在的异常
 *
 * @author : john.peng created on date : 2017/11/12
 */
public class EntityNotFoundException extends EntityAccessException {
    /**
     * 默认的构造函数
     *
     * @param message 异常内容
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
