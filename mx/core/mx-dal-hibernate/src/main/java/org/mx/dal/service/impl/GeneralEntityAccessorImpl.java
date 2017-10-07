package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralEntityAccessor;
import org.mx.dal.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * Created by john on 2017/10/6.
 */
public class GeneralEntityAccessorImpl implements GeneralEntityAccessor {
    private static final Log logger = LogFactory.getLog(GeneralEntityAccessorImpl.class);

    @Autowired
    protected EntityManager entityManager = null;

    @Autowired
    @Qualifier("sessionDataThreadLocal")
    protected SessionDataStore sessionDataStore = null;

    protected <T extends Base> Class<T> getEntityClass(Class<T> entityInterfaceClass) throws ClassNotFoundException {
        String entityClassName = String.format("%sEntity", entityInterfaceClass.getName());
        return (Class<T>) Class.forName(entityClassName);
    }

    @Override
    public <T extends Base> long count(Class<T> entityInterfaceClass) throws EntityAccessException {
        // TODO
        return 0;
    }

    @Override
    public <T extends Base> List<T> list(Class<T> entityInterfaceClass) throws EntityAccessException {
        // TODO
        return null;
    }

    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass) throws EntityAccessException {
        // TODO
        return null;
    }

    public <T extends Base> T getById(String id, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            T t = entityManager.find(clazz, id);
            return t;
        } catch (ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Entity interface[%s] not be implemented.",
                        entityClass.getName()), ex);
            }
            return null;
        }
    }

    @Override
    public <T extends Base> T getById(String id, Class<T> entityInterfaceClass) throws EntityAccessException {
        return getById(id, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityInterfaceClass) throws EntityAccessException {
        // TODO
        return null;
    }

    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityInterfaceClass) throws EntityAccessException {
        // TODO
        return null;
    }

    @Override
    public <T extends Base> T save(T t) throws EntityAccessException {
        if (StringUtils.isBlank(t.getId())) {
            // 新增操作
            t.setId(null);
            t.setCreatedTime(new Date().getTime());
        } else {
            // 修改操作
            T old = getById(t.getId(), (Class<T>)t.getClass(), false);
            if (old == null) {
                throw new EntityAccessException(String.format("The entity[%s] not found.", t.getId()));
            }
            t.setCreatedTime(old.getCreatedTime());
            if (t instanceof BaseDict) {
                ((BaseDict)t).setCode(((BaseDict)old).getCode());
            }
        }
        t.setUpdatedTime(new Date().getTime());
        t.setOperator(sessionDataStore.getCurrentUserCode());
        entityManager.persist(t);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save entity success, entity: %s.", t));
        }
        return getById(t.getId(), (Class<T>)t.getClass(), false);
    }

    @Override
    public <T extends Base> T remove(T t) throws EntityAccessException {
        return remove(t, true);
    }

    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) throws EntityAccessException {
        if (logicRemove) {
            // 逻辑删除
            t.setValid(false);
            return save(t);
        } else {
            // 物理删除
            entityManager.remove(t);
            return t;
        }
    }
}
