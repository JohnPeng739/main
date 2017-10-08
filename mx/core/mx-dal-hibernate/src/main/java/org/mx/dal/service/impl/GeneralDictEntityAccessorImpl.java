package org.mx.dal.service.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralDictEntityAccessor;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by john on 2017/10/6.
 */
@Component("generalDictEntityAccessor")
public class GeneralDictEntityAccessorImpl extends GeneralEntityAccessorImpl implements GeneralDictEntityAccessor {
    private static final Log logger = LogFactory.getLog(GeneralDictEntityAccessorImpl.class);

    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> entityInterfaceClass) throws EntityAccessException {
        return getByCode(code, entityInterfaceClass, true);
    }

    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = (Class<T>) super.getEntityClass(entityClass);
            }
            Query query = entityManager.createQuery(String.format("SELECT dict FROM %s dict WHERE dict.code = :code",
                    clazz.getName()));
            query.setParameter("code", code);
            List<T> result = query.getResultList();
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
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Get dict entity[%s] by code[%s] fail.",
                    entityClass.getName(), code), ex);
        }
    }
}
