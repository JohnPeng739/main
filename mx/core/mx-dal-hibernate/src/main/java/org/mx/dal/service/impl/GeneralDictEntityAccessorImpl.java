package org.mx.dal.service.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.GeneralDictEntityAccessor;
import org.mx.dal.service.GeneralEntityAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于Hibernate的JPA字典类实体基础访问的DAL实现
 *
 * @author : john.peng date : 2017/10/6
 * @see GeneralEntityAccessorImpl
 * @see GeneralDictEntityAccessor
 */
@Component("generalDictEntityAccessorHibernate")
public class GeneralDictEntityAccessorImpl extends GeneralEntityAccessorImpl implements GeneralDictEntityAccessor {
    private static final Log logger = LogFactory.getLog(GeneralDictEntityAccessorImpl.class);

    /**
     * {@inheritDoc}
     *
     * @see GeneralDictAccessor#getByCode(String, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> entityInterfaceClass) throws UserInterfaceDalErrorException {
        return getByCode2(code, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralDictEntityAccessor#getByCode2(String, Class, boolean)
     * @see GeneralEntityAccessor#find2(List, Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends BaseDict> T getByCode2(String code, Class<T> entityClass, boolean isInterfaceClass) throws UserInterfaceDalErrorException {
        List<ConditionTuple> tuples = new ArrayList<>();
        tuples.add(new ConditionTuple("code", code));
        List<T> result = super.find2(tuples, entityClass, isInterfaceClass);
        if (result != null && result.size() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Found %d dict entity, entity: %s, code: %s.",
                        result.size(), entityClass.getName(), code));
            }
            return result.get(0);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The dict entity not found, entity: %s, code: %s.",
                        entityClass.getName(), code));
            }
            return null;
        }
    }
}
