package org.mx.dal.service.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于Hibernate的JPA字典类实体基础访问的DAL实现
 *
 * @author : john.peng date : 2017/10/6
 * @see GeneralAccessorImpl
 * @see GeneralDictAccessor
 */
@Component("generalDictAccessorJpa")
public class GeneralDictAccessorImpl extends GeneralAccessorImpl implements GeneralDictAccessor {
    private static final Log logger = LogFactory.getLog(GeneralDictAccessorImpl.class);

    /**
     * {@inheritDoc}
     *
     * @see GeneralDictAccessor#getByCode(String, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> clazz) throws UserInterfaceDalErrorException {
        List<ConditionTuple> tuples = new ArrayList<>();
        tuples.add(new ConditionTuple("code", code));
        List<T> result = super.find(tuples, clazz);
        if (result != null && result.size() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Found %d dict entity, entity: %s, code: %s.",
                        result.size(), clazz.getName(), code));
            }
            return result.get(0);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The dict entity not found, entity: %s, code: %s.",
                        clazz.getName(), code));
            }
            return null;
        }
    }
}
