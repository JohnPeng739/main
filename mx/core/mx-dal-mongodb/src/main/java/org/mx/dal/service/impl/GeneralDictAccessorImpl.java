package org.mx.dal.service.impl;

import org.mx.dal.EntityFactory;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * 基于Mongodb实现的基础字典类实体访问实现类
 *
 * @author : john.peng date : 2017/10/8
 * @see GeneralAccessorImpl
 * @see GeneralDictAccessor
 */
@Component("generalDictAccessorMongodb")
public class GeneralDictAccessorImpl extends GeneralAccessorImpl implements GeneralDictAccessor {
    /**
     * {@inheritDoc}
     *
     * @see GeneralDictAccessor#getByCode(String, Class)
     */
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> clazz)
            throws UserInterfaceDalErrorException {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            return template.findOne(Query.query(where("code").is(code)), clazz);
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }
}
