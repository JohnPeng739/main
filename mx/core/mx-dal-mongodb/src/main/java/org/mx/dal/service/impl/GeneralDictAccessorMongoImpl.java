package org.mx.dal.service.impl;

import org.mx.dal.EntityFactory;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.spring.session.SessionDataStore;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

/**
 * 基于Mongodb实现的基础字典类实体访问实现类
 *
 * @author : john.peng date : 2017/10/8
 * @see GeneralAccessorMongoImpl
 * @see GeneralDictAccessor
 */
public class GeneralDictAccessorMongoImpl extends GeneralAccessorMongoImpl implements GeneralDictAccessor {
    /**
     * 默认的构造函数
     *
     * @param template         MongodbTemplate
     * @param sessionDataStore 会话数据存储器
     */
    public GeneralDictAccessorMongoImpl(MongoTemplate template, SessionDataStore sessionDataStore) {
        super(template, sessionDataStore);
    }

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
            return super.findOne(ConditionTuple.eq("code", code), clazz);
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }
}
