package org.mx.dal.service.impl;

import org.mx.dal.entity.BaseDict;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralDictEntityAccessor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by john on 2017/10/8.
 */
@Component("generalDictEntityAccessorMongodb")
public class GeneralDictEntityAccessorImpl extends GeneralEntityAccessorImpl implements GeneralDictEntityAccessor {
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

    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> entityInterfaceClass) throws EntityAccessException {
        return getByCode(code, entityInterfaceClass, true);
    }
}
