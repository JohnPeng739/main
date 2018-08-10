package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.entity.BaseDictTree;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

/**
 * 基于Hibernate的JPA实体基础访问的DAL实现
 *
 * @author : john.peng date : 2017/10/6
 * @see GeneralAccessor
 */
public class GeneralAccessorImpl implements GeneralAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorImpl.class);

    @PersistenceContext
    protected EntityManager entityManager = null;

    protected SessionDataStore sessionDataStore;

    public GeneralAccessorImpl() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param sessionDataStore 会话数据服务接口
     */
    public GeneralAccessorImpl(@Qualifier("sessionDataThreadLocal") SessionDataStore sessionDataStore) {
        super();
        this.sessionDataStore = sessionDataStore;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) {
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
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            if (pagination == null) {
                pagination = new Pagination();
            }
            pagination.setTotal((int) count(clazz, isValid));
            Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity %s ", clazz.getName(),
                    isValid ? "WHERE entity.valid = TRUE" : ""));
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
    public <T extends Base> long count(Class<T> clazz) {
        return count(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Class<T> clazz) {
        return list(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> List<T> list(Class<T> clazz, boolean isValid) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            Query query = entityManager.createQuery(String.format("SELECT entity FROM %s entity %s ", clazz.getName(),
                    isValid ? "WHERE entity.valid = TRUE" : ""));
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
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz) {
        return list(pagination, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            return entityManager.find(clazz, id);
        } catch (ClassNotFoundException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Entity interface[%s] not be implemented.",
                        clazz.getName()), ex);
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> Predicate createCondition(CriteriaBuilder cb, Root<T> root, ConditionTuple tuple) {
        switch (tuple.operate) {
            case CONTAIN:
                return cb.like((Expression<String>) getField(tuple.field, root), String.format("%%%s%%", tuple.value));
            case PREFIX:
                return cb.like((Expression<String>) getField(tuple.field, root), String.format("%s%%", tuple.value));
            case EQ:
                return cb.equal(getField(tuple.field, root), tuple.value);
            case LT:
                return cb.lt((Expression<? extends Number>) getField(tuple.field, root), (Number) tuple.value);
            case GT:
                return cb.gt((Expression<? extends Number>) getField(tuple.field, root), (Number) tuple.value);
            case LTE:
                return cb.le((Expression<? extends Number>) getField(tuple.field, root), (Number) tuple.value);
            case GTE:
                return cb.ge((Expression<? extends Number>) getField(tuple.field, root), (Number) tuple.value);
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported the operate type: %s.", tuple.operate));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
        }
    }

    private <T extends Base> Predicate createGroupPredicate(CriteriaBuilder cb, Root<T> root, ConditionGroup group) {
        if (group.getItems().size() == 1) {
            return createCondition(cb, root, (ConditionTuple) group.getItems().get(0));
        }
        Predicate[] predicates = new Predicate[group.getItems().size()];
        for (int index = 0; index < group.getItems().size(); index++) {
            predicates[index] = createGroupPredicate(cb, root, group.getItems().get(index));
        }
        return group.getOperateType() == ConditionGroup.OperateType.AND ? cb.and(predicates) : cb.or(predicates);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(ConditionGroup, Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> find(ConditionGroup group, Class<T> clazz) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
            Root<T> root = criteriaQuery.from(clazz);
            criteriaQuery.where(createGroupPredicate(cb, root, group));
            Query query = entityManager.createQuery(criteriaQuery);
            return query.getResultList();
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * 根据传入的field，获取真正的比较字段。
     *
     * @param field 字段名，支持如"src.id"之类的对象操作
     * @param root  根
     * @param <T>   操作的范型类型
     * @return 真正的比较字段
     */
    private <T extends Base> Path<?> getField(String field, Root<T> root) {
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
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> clazz) {
        ConditionGroup group = ConditionGroup.and();
        if (tuples != null && !tuples.isEmpty()) {
            tuples.forEach(group::add);
        }
        List<T> result = find(group, clazz);
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
    public <T extends Base> T save(T t) {
        return save(t, true);
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> T save(T t, boolean needFlush) {
        if (t.getUpdatedTime() <= 0) {
            t.setUpdatedTime(new Date().getTime());
        }
        if (StringUtils.isBlank(t.getOperator())) {
            t.setOperator(sessionDataStore.getCurrentUserCode());
        }
        Class<T> clazz = (Class<T>) t.getClass();
        if (t instanceof BaseDictTree) {
            BaseDictTree parent = ((BaseDictTree) t).getParent();
            if (!StringUtils.isBlank(((BaseDictTree) t).getParentId())) {
                T p = getById(((BaseDictTree) t).getParentId(), clazz);
                ((BaseDictTree) t).setParent((BaseDictTree) p);
            }
        }
        if (StringUtils.isBlank(t.getId())) {
            // 新增操作
            t.setId(DigestUtils.uuid());
            if (t.getCreatedTime() <= 0) {
                t.setCreatedTime(new Date().getTime());
            }
            entityManager.persist(t);
        } else {
            // 修改操作
            T old = getById(t.getId(), clazz);
            if (old != null) {
                t.setCreatedTime(old.getCreatedTime());
                if (t instanceof BaseDict) {
                    ((BaseDict) t).setCode(((BaseDict) old).getCode());
                }
                entityManager.merge(t);
            } else {
                // 新增操作，使用传入的ID
                if (t.getCreatedTime() <= 0) {
                    t.setCreatedTime(new Date().getTime());
                }
                entityManager.persist(t);
            }
        }
        if (needFlush) {
            entityManager.flush();
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
     * @see GeneralAccessor#save(List)
     */
    @Transactional
    @Override
    public <T extends Base> List<T> save(List<T> ts) {
        if (ts != null && !ts.isEmpty()) {
            for (T t : ts) {
                save(t, false);
            }
        }
        entityManager.flush();
        return ts;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#clear(Class)
     */
    @Transactional
    @Override
    public <T extends Base> void clear(Class<T> clazz) {
        entityManager.clear();
        entityManager.createQuery(String.format("DELETE %s", clazz.getName())).executeUpdate();
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class)
     */
    @Transactional
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz) {
        return remove(id, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class, boolean)
     */
    @Transactional
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz, boolean logicRemove) {
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
    public <T extends Base> T remove(T t) {
        return remove(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base, boolean)
     */
    @Transactional()
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) {
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
