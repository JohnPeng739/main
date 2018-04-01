package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 基于Hibernate的JPA实体基础访问的DAL实现
 *
 * @author : john.peng date : 2017/10/6
 * @see GeneralAccessor
 */
@Component("generalAccessorJpa")
public class GeneralAccessorImpl implements GeneralAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorImpl.class);

    @PersistenceContext
    protected EntityManager entityManager = null;

    @Autowired
    @Qualifier("sessionDataThreadLocal")
    protected SessionDataStore sessionDataStore = null;

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            Query query = entityManager.createQuery(String.format("SELECT COUNT(entity) FROM %s entity %s",
                    clazz.getName(), isValid ? "WHERE entity.valid = TRUE" : ""));
            long count = (long) query.getSingleResult();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Count %d %s entity[%s].", count, isValid ? "valid" : "", clazz.getName()));
            }
            return count;
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid)
            throws UserInterfaceDalErrorException {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            if (pagination == null) {
                pagination = new Pagination();
            }
            pagination.setTotal((int) count(clazz, isValid));
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
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> long count(Class<T> clazz) throws UserInterfaceDalErrorException {
        return count(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Class<T> clazz) throws UserInterfaceDalErrorException {
        return list(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity %s " +
                    "ORDER BY entity.createdTime DESC", clazz.getName(), isValid ? "WHERE entity.valid = TRUE" : ""));
            List<T> result = query.getResultList();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("List %d %s entity[%s].", result.size(), isValid ? "valid" : "", clazz.getName()));
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz)
            throws UserInterfaceDalErrorException {
        return list(pagination, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) throws UserInterfaceDalErrorException {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            T t = entityManager.find(clazz, id);
            return t;
        } catch (ClassNotFoundException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Entity interface[%s] not be implemented.",
                        clazz.getName()), ex);
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(List, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> clazz)
            throws UserInterfaceDalErrorException {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
            Root<T> root = criteriaQuery.from(clazz);
            List<Predicate> conditions = new ArrayList<>();
            if (tuples != null && tuples.size() > 0) {
                tuples.forEach(tuple -> conditions.add(cb.equal(getField(tuple.field, root), tuple.value)));
            }
            criteriaQuery.where(conditions.toArray(new Predicate[0]));
            Query query = entityManager.createQuery(criteriaQuery);
            List<T> result = query.getResultList();
            return result;
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * 根据传入的filed，获取真正的比较字段。
     *
     * @param field 字段名，支持如"src.id"之类的对象操作
     * @param root  根
     * @param <T>   操作的范型类型
     * @return 真正的比较字段
     */
    private <T extends Base> Path<T> getField(String field, Root<T> root) {
        String[] path = field.split("\\.");
        Path<T> result = root;
        for (String p : path) {
            result = result.get(p);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#findOne(List, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> clazz)
            throws UserInterfaceDalErrorException {
        List<T> result = find(tuples, clazz);
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
    @Transactional
    @Override
    public <T extends Base> T save(T t) throws UserInterfaceDalErrorException {
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
            T old = getById(t.getId(), (Class<T>) t.getClass());
            if (old == null) {
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_NOT_FOUND);
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
     * @see GeneralAccessor#remove(String, Class)
     */
    @Transactional
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz) throws UserInterfaceDalErrorException {
        return remove(id, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class, boolean)
     */
    @Transactional
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz, boolean logicRemove)
            throws UserInterfaceDalErrorException {
        T t = getById(id, clazz);
        if (t == null) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_NOT_FOUND);
        }
        return remove(t, logicRemove);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base)
     */
    @Transactional()
    @Override
    public <T extends Base> T remove(T t) throws UserInterfaceDalErrorException {
        return remove(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base, boolean)
     */
    @Transactional()
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) throws UserInterfaceDalErrorException {
        T removeEntity = getById(t.getId(), (Class<T>) t.getClass());
        if (logicRemove) {
            // 逻辑删除
            removeEntity.setValid(false);
            t = save(removeEntity);
            return t;
        } else {
            // 物理删除
            entityManager.remove(removeEntity);
            entityManager.flush();
            return t;
        }
    }
}
