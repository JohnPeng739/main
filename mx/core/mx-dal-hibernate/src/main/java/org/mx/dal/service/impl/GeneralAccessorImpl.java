package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.AbstractGeneralAccessor;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.spring.session.SessionDataStore;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于Hibernate的JPA实体基础访问的DAL实现
 *
 * @author : john.peng date : 2017/10/6
 * @see GeneralAccessor
 */
public class GeneralAccessorImpl extends AbstractGeneralAccessor implements GeneralAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorImpl.class);

    @PersistenceContext
    protected EntityManager entityManager = null;

    /**
     * 默认的构造函数
     *
     * @param sessionDataStore 会话数据服务接口
     */
    public GeneralAccessorImpl(SessionDataStore sessionDataStore) {
        super();
        super.sessionDataStore = sessionDataStore;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) {
        return count(isValid ? ConditionTuple.eq("valid", true) : null, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(ConditionGroup, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> long count(ConditionGroup group, Class<T> clazz) {
        if (clazz.isInterface()) {
            try {
                clazz = EntityFactory.getEntityClass(clazz);
            } catch (ClassNotFoundException ex) {
                throw new UserInterfaceDalErrorException(
                        UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL
                );
            }
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> rootCount = countQuery.from(clazz);
        countQuery.select(cb.count(rootCount));
        if (group != null) {
            countQuery.where(createGroupPredicate(cb, rootCount, group));
        }
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(Pagination, ConditionGroup, RecordOrderGroup, Class)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) {
        return find(pagination, isValid ? ConditionTuple.eq("valid", true) : null, null, clazz);
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
            Query query;
            if (isValid) {
                return find(ConditionTuple.eq("valid", true), clazz);
            } else {
                query = entityManager.createQuery(String.format("SELECT entity FROM %s entity", clazz.getName()));
                List<T> result = query.getResultList();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("List %d entity[%s].", result.size(), clazz.getName()));
                }
                return result;
            }
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
            case IS_NULL:
                return cb.isNull(getField(tuple.field, root));
            case IS_NOT_NULL:
                return cb.isNotNull(getField(tuple.field, root));
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
    @Transactional(readOnly = true)
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> find(ConditionGroup group, Class<T> clazz) {
        return find(null, group, null, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(Pagination, ConditionGroup, RecordOrderGroup, Class)
     */
    @Transactional(readOnly = true)
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> find(Pagination pagination, ConditionGroup group, RecordOrderGroup orderGroup,
                                         Class<T> clazz) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            if (pagination != null) {
                CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
                Root<T> rootCount = countQuery.from(clazz);
                countQuery.select(cb.count(rootCount));
                if (group != null) {
                    countQuery.where(createGroupPredicate(cb, rootCount, group));
                }
                pagination.setTotal(entityManager.createQuery(countQuery).getSingleResult().intValue());
            }

            CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
            Root<T> root = criteriaQuery.from(clazz);
            if (group != null) {
                criteriaQuery.where(createGroupPredicate(cb, root, group));
            }
            if (orderGroup != null && !orderGroup.getOrders().isEmpty()) {
                List<Order> orders = new ArrayList<>();
                orderGroup.getOrders().forEach(order -> {
                    if (order.getType() == RecordOrder.OrderType.ASC) {
                        orders.add(cb.asc(root.get(order.getField())))
                        ;
                    } else {
                        orders.add(cb.desc(root.get(order.getField())));
                    }
                });
                criteriaQuery.orderBy(orders);
            }
            Query query = entityManager.createQuery(criteriaQuery);
            if (pagination != null) {
                query.setFirstResult((pagination.getPage() - 1) * pagination.getSize());
                query.setMaxResults(pagination.getSize());
            }
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
     * @see GeneralAccessor#findOne(ConditionGroup, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T findOne(ConditionGroup group, Class<T> clazz) {
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

    private <T extends Base> T save(T t, boolean needFlush) {
        T old = prepareSave(t);
        if (old == null) {
            // 新增操作
            entityManager.persist(t);
        } else {
            // 修改操作
            entityManager.merge(t);
        }
        if (needFlush) {
            // 为了防止管理对象没有及时更新状态，这里根据需要强制刷新一次
            entityManager.flush();
            entityManager.clear();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save(%s) entity success, entity: %s.", old == null ? "INSERT" : "UPDATE", t));
        }
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
            // 必须要先处理存在性判断，否则checkExist中发起的查询将破坏事务性二降低性能
            List<Boolean> isNews = new ArrayList<>(ts.size());
            for (T t : ts) {
                isNews.add(prepareSave(t) == null);
            }
            for (int index = 0; index < ts.size(); index++) {
                T t = ts.get(index);
                boolean isNew = isNews.get(index);
                if (isNew) {
                    // 新增操作
                    entityManager.persist(t);
                } else {
                    // 修改操作
                    entityManager.merge(t);
                }
            }
        }
        entityManager.flush();
        entityManager.clear();
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
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) {
        return remove(t, logicRemove, true);
    }

    @Transactional()
    @Override
    public <T extends Base> List<T> remove(List<T> ts, boolean logicRemove) {
        for (T t : ts) {
            remove(t, logicRemove, false);
        }
        entityManager.flush();
        return ts;
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> T remove(T t, boolean logicRemove, boolean needFlush) {
        T removeEntity = getById(t.getId(), (Class<T>) t.getClass());
        if (logicRemove) {
            // 逻辑删除
            removeEntity.setValid(false);
            t = save(removeEntity, needFlush);
            return t;
        } else {
            // 物理删除
            entityManager.remove(removeEntity);
            if (needFlush) {
                entityManager.flush();
            }
            return t;
        }
    }
}
