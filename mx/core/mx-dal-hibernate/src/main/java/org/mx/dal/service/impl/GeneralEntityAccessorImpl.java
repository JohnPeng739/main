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
import javax.persistence.Query;
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
        return count(entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> long count(Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            Query query = entityManager.createQuery(String.format("SELECT COUNT(entity) FROM %s entity",
                    clazz.getName()));
            long count = (long) query.getSingleResult();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Count %d entity[%s].", count, clazz.getName()));
            }
            return count;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Count entity fail, entity: %s.", entityClass.getName()), ex);
        }
    }

    @Override
    public <T extends Base> List<T> list(Class<T> entityInterfaceClass) throws EntityAccessException {
        return list(entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> List<T> list(Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity", clazz.getName()));
            List<T> result = query.getResultList();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("List %d entity[%s].", result.size(), clazz.getName()));
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("List entity fail, entity: %s.", entityClass.getName()), ex);
        }
    }

    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass)
            throws EntityAccessException {
        return list(pagination, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            if (pagination == null) {
                pagination = new Pagination();
            }
            pagination.setTotal((int) count(clazz, false));
            Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity"));
            query.setFirstResult((pagination.getPage() - 1) * pagination.getSize());
            query.setMaxResults(pagination.getSize());
            List<T> result = query.getResultList();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Pagination list %d entity[%s], pagination: %s",
                        result.size(), clazz.getName(), pagination));
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Pagination list entity[%s] fail.",
                    entityClass.getName()), ex);
        }
    }

    @Override
    public <T extends Base> T getById(String id, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException {
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
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws EntityAccessException {
        return find(tuples, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException {
        // TODO
        return null;
    }

    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws EntityAccessException {
        return findOne(tuples, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException {
        List<T> result = find(tuples, entityClass, isInterfaceClass);
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public <T extends Base> T save(T t) throws EntityAccessException {
        if (StringUtils.isBlank(t.getId())) {
            // 新增操作
            t.setId(null);
            t.setCreatedTime(new Date().getTime());
        } else {
            // 修改操作
            T old = getById(t.getId(), (Class<T>) t.getClass(), false);
            if (old == null) {
                throw new EntityAccessException(String.format("The entity[%s] not found.", t.getId()));
            }
            t.setCreatedTime(old.getCreatedTime());
            if (t instanceof BaseDict) {
                ((BaseDict) t).setCode(((BaseDict) old).getCode());
            }
        }
        t.setUpdatedTime(new Date().getTime());
        t.setOperator(sessionDataStore.getCurrentUserCode());
        entityManager.persist(t);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save entity success, entity: %s.", t));
        }
        return getById(t.getId(), (Class<T>) t.getClass(), false);
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
