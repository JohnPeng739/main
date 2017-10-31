package org.mx.dal.service;

import org.mx.dal.entity.BaseDict;
import org.mx.dal.exception.EntityAccessException;

/**
 * 基于Hibernate的JPA基本字典类实体访问的DAL接口定义
 *
 * @author : john.peng date : 2017/10/7
 * @see GeneralDictAccessor
 */
public interface GeneralDictEntityAccessor extends GeneralDictAccessor {
    /**
     * 根据实体的唯一代码获取指定的实体。
     *
     * @param code             实体代码
     * @param entityClass      实体类
     * @param isInterfaceClass 如果为true，表示entityClass指定的类为实体接口类，否则表示真实的实体类
     * @param <T>              实现BaseDict接口的泛型对象类型
     * @return 实体对象，如果不存在，则返回null。
     * @throws EntityAccessException 获取过程中发生的异常
     */
    <T extends BaseDict> T getByCode2(String code, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException;
}
