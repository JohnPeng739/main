package org.mx.dal.service.impl;

import org.mx.dal.entity.BaseDict;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.GeneralDictEntityAccessor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * 基于Mongodb实现的基础字典类实体访问实现类
 *
 * @author : john.peng date : 2017/10/8
 * @see GeneralEntityAccessorImpl
 * @see GeneralDictEntityAccessor
 */
@Component("generalDictEntityAccessorMongodb")
public class GeneralDictEntityAccessorImpl extends GeneralEntityAccessorImpl implements GeneralDictEntityAccessor {
    /**
     * {@inheritDoc}
     *
     * @see GeneralDictEntityAccessor#getByCode(String, Class, boolean)
     */
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = super.getEntityClass(clazz);
            }
            return template.findOne(Query.query(where("code").is(code)), clazz);
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Get entity[%s] by code[%s] fail.",
                    entityClass.getName(), code), ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralDictAccessor#getByCode(String, Class)
     */
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> entityInterfaceClass) throws EntityAccessException {
        return getByCode(code, entityInterfaceClass, true);
    }
}
