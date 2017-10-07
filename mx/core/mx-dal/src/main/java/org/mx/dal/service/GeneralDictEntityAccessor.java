package org.mx.dal.service;


import org.mx.dal.entity.BaseDict;
import org.mx.dal.exception.EntityAccessException;

/**
 * 字典类实体的存取接口定义，实体接口必须继承BaseDict；本接口继承了GeneralEntityAccessor。
 *
 * @see GeneralEntityAccessor Created by john on 2017/8/18.
 */
public interface GeneralDictEntityAccessor extends GeneralEntityAccessor {
    /**
     * 根据实体的唯一代码获取指定的实体。
     *
     * @param code                 实体代码
     * @param entityInterfaceClass 实体接口类
     * @return 实体对象，如果不存在，则返回null。
     * @throws EntityAccessException 获取过程中发生的异常
     */
    <T extends BaseDict> T getByCode(String code, Class<T> entityInterfaceClass) throws EntityAccessException;
}
