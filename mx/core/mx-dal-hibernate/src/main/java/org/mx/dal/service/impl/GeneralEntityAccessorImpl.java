package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralEntityAccessor;
import org.mx.dal.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 基于Hibernate的JPA实体基础访问的DAL实现
 *
 * @author : john.peng date : 2017/10/6
 * @see GeneralEntityAccessor
 */
@Component("generalEntityAccessorHibernate")
public class GeneralEntityAccessorImpl implements GeneralEntityAccessor {
    private static final Log logger = LogFactory.getLog(GeneralEntityAccessorImpl.class);

    @PersistenceContext
    protected EntityManager entityManager = null;

    @Autowired
    @Qualifier("sessionDataThreadLocal")
    protected SessionDataStore sessionDataStore = null;

    /**
     * 根据指定的实体接口定义类返回对应的实体类定义
     *
     * @param entityInterfaceClass 实体接口定义类
     * @param <T>                  泛型类型
     * @return 实体类定义
     * @throws ClassNotFoundException 指定的实体类没有定义
     */
    protected <T extends Base> Class<T> getEntityClass(Class<T> entityInterfaceClass) throws ClassNotFoundException {
        String entityClassName = String.format("%sEntity", entityInterfaceClass.getName());
        return (Class<T>) Class.forName(entityClassName);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Override
    public <T extends Base> long count(Class<T> entityClass, boolean isValid) throws EntityAccessException {
        return count2(entityClass, true, isValid);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#count2(Class, boolean, boolean)
     */
    @Override
    public <T extends Base> long count2(Class<T> entityClass, boolean isInterfaceClass, boolean isValid) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            Query query = entityManager.createQuery(String.format("SELECT COUNT(entity) FROM %s entity %s",
                    clazz.getName(), isValid ? "WHERE entity.valid = TRUE" : ""));
            long count = (long) query.getSingleResult();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Count %d %s entity[%s].", count, isValid ? "valid" : "", clazz.getName()));
            }
            return count;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Count entity fail, entity: %s.", entityClass.getName()), ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list2(Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        return list2(entityClass, isInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass, boolean isValid) throws EntityAccessException {
        return list2(pagination, entityInterfaceClass, true, isValid);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Class, boolean, boolean)
     */
    @Override
    public <T extends Base> List<T> list2(Class<T> entityClass, boolean isInterfaceClass, boolean isValid) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity %s " +
                    "ORDER BY entity.createdTime DESC", clazz.getName(), isValid ? "WHERE entity.valid = TRUE" : ""));
            List<T> result = query.getResultList();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("List %d %s entity[%s].", result.size(), isValid ? "valid" : "", clazz.getName()));
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("List entity fail, entity: %s.", entityClass.getName()), ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Pagination, Class, boolean, boolean)
     */
    @Override
    public <T extends Base> List<T> list2(Pagination pagination, Class<T> entityClass, boolean isInterfaceClass, boolean isValid) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            if (pagination == null) {
                pagination = new Pagination();
            }
            pagination.setTotal((int) count(clazz, false));
            Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity %s " +
                    "ORDER BY entity.createdTime DESC", clazz.getName(), isValid ? "WHERE entity.valid = TRUE" : ""));
            query.setFirstResult((pagination.getPage() - 1) * pagination.getSize());
            query.setMaxResults(pagination.getSize());
            List<T> result = query.getResultList();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Pagination list %d %s entity[%s], pagination: %s",
                        result.size(), isValid ? "valid" : "", clazz.getName(), pagination));
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Pagination list entity[%s] fail.",
                    entityClass.getName()), ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> long count(Class<T> entityInterfaceClass) throws EntityAccessException {
        return count(entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#count2(Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> long count2(Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        return count2(entityClass, isInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Class<T> entityInterfaceClass) throws EntityAccessException {
        return list(entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        return list2(entityClass, isInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass)
            throws EntityAccessException {
        return list(pagination, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Pagination, Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list2(Pagination pagination, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException {
        return list2(pagination, entityClass, isInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#getById2(String, Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T getById2(String id, Class<T> entityClass, boolean isInterfaceClass)
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

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T getById(String id, Class<T> entityInterfaceClass) throws EntityAccessException {
        return getById2(id, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(List, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws EntityAccessException {
        return find2(tuples, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#find2(List, Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> find2(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(entityClass);
            }
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
            Root<T> root = criteriaQuery.from(clazz);
            List<Predicate> conditions = new ArrayList<>();
            if (tuples != null && tuples.size() > 0) {
                tuples.forEach(tuple -> conditions.add(cb.equal(root.get(tuple.field), tuple.value)));
            }
            criteriaQuery.where(conditions.toArray(new Predicate[0]));
            Query query = entityManager.createQuery(criteriaQuery);
            List<T> result = query.getResultList();
            return result;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Find entity fail, entity: %s.", entityClass.getName()), ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#findOne(List, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws EntityAccessException {
        return findOne2(tuples, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#findOne2(List, Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T findOne2(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException {
        List<T> result = find2(tuples, entityClass, isInterfaceClass);
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(Base)
     */
    @Transactional()
    @Override
    public <T extends Base> T save(T t) throws EntityAccessException {
        t.setUpdatedTime(new Date().getTime());
        t.setOperator(sessionDataStore.getCurrentUserCode());
        if (StringUtils.isBlank(t.getId())) {
            // 新增操作
            t.setId(null);
            t.setCreatedTime(new Date().getTime());
            entityManager.persist(t);
            entityManager.flush();
        } else {
            // 修改操作
            T old = getById2(t.getId(), (Class<T>) t.getClass(), false);
            if (old == null) {
                throw new EntityAccessException(String.format("The entity[%s] not found.", t.getId()));
            }
            t.setCreatedTime(old.getCreatedTime());
            if (t instanceof BaseDict) {
                ((BaseDict) t).setCode(((BaseDict) old).getCode());
            }
            entityManager.merge(t);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save entity success, entity: %s.", t));
        }
        // return getById(t.getId(), (Class<T>) t.getClass(), false);
        // 为了提高性能，直接返回
        return t;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base)
     */
    @Transactional()
    @Override
    public <T extends Base> T remove(T t) throws EntityAccessException {
        return remove(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base, boolean)
     */
    @Transactional()
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) throws EntityAccessException {
        T removeEntity = getById2(t.getId(), (Class<T>) t.getClass(), false);
        if (logicRemove) {
            // 逻辑删除
            removeEntity.setValid(false);
            return save(removeEntity);
        } else {
            // 物理删除
            entityManager.remove(removeEntity);
            entityManager.flush();
            return t;
        }
    }
}
