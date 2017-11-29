package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralEntityAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 基于Mongodb实现的基础实体访问实现类
 *
 * @author : john.peng date : 2017/10/8
 * @see GeneralEntityAccessor
 */
@Component("generalEntityAccessorMongodb")
public class GeneralEntityAccessorImpl implements GeneralEntityAccessor {
    private static final Log logger = LogFactory.getLog(GeneralEntityAccessorImpl.class);

    @Autowired
    protected MongoTemplate template = null;

    @Autowired
    @Qualifier("sessionDataThreadLocal")
    private SessionDataStore sessionDataStore = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * 根据指定的实体接口定义返回对应的实体定义类
     *
     * @param entityInterfaceClass 实体接口类
     * @param <T>                  泛型类型
     * @return 实体类
     * @throws ClassNotFoundException 实体类型没有定义
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
    public <T extends Base> long count(Class<T> entityInterfaceClass, boolean isValid) throws UserInterfaceDalErrorException {
        return count2(entityInterfaceClass, true, isValid);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#count2(Class, boolean, boolean)
     */
    @Override
    public <T extends Base> long count2(Class<T> entityClass, boolean isInterfaceClass, boolean isValid) throws UserInterfaceDalErrorException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(clazz);
            }
            long count = 0;
            if (isValid) {
                count = template.count(query(where("valid").is(true)), clazz);
            } else {
                count = template.count(new Query(), clazz);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Count %d %s entity[%s].", count, isValid ? "valid" : "", entityClass.getName()));
            }
            return count;
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class)
     */
    @Override
    public <T extends Base> long count(Class<T> entityInterfaceClass) throws UserInterfaceDalErrorException {
        return count2(entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#count2(Class, boolean)
     */
    @Override
    public <T extends Base> long count2(Class<T> entityClass, boolean isInterfaceClass)
            throws UserInterfaceDalErrorException {
        return count2(entityClass, isInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> entityInterfaceClass, boolean isValid)
            throws UserInterfaceDalErrorException {
        return list2(entityInterfaceClass, true, isValid);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Class, boolean, boolean)
     */
    @Override
    public <T extends Base> List<T> list2(Class<T> entityClass, boolean isInterfaceClass, boolean isValid)
            throws UserInterfaceDalErrorException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(clazz);
            }
            List<T> result;
            if (isValid) {
                List<ConditionTuple> tuples = new ArrayList<>();
                tuples.add(new ConditionTuple("valid", true));
                result = find2(tuples, clazz, false);
            } else {
                result = template.findAll(clazz);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("List %d entity[%s].", result.size(), entityClass.getName()));
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass, boolean isValid)
            throws UserInterfaceDalErrorException {
        return list2(pagination, entityInterfaceClass, true, isValid);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Pagination, Class, boolean, boolean)
     */
    @Override
    public <T extends Base> List<T> list2(Pagination pagination, Class<T> entityClass, boolean isInterfaceClass, boolean isValid)
            throws UserInterfaceDalErrorException {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(clazz);
            }
            pagination.setTotal((int) count2(clazz, false, isValid));
            int skip = (pagination.getPage() - 1) * pagination.getSize();
            int limit = pagination.getSize();
            if (isValid) {
                return template.find(query(where("valid").is(true)).skip(skip).limit(limit), entityClass);
            } else {
                return template.find(new Query().skip(skip).limit(limit), entityClass);
            }
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> entityInterfaceClass) throws UserInterfaceDalErrorException {
        return list(entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list2(Class<T> entityClass, boolean isInterfaceClass)
            throws UserInterfaceDalErrorException {
        return list2(entityClass, isInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass)
            throws UserInterfaceDalErrorException {
        return list(pagination, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#list2(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list2(Pagination pagination, Class<T> entityClass, boolean isInterfaceClass)
            throws UserInterfaceDalErrorException {
        return list2(pagination, entityClass, isInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Override
    public <T extends Base> T getById(String id, Class<T> entityInterfaceClass) throws UserInterfaceDalErrorException {
        return getById2(id, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#getById2(String, Class, boolean)
     */
    @Override
    public <T extends Base> T getById2(String id, Class<T> entityClass, boolean isInterfaceClass)
            throws UserInterfaceDalErrorException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(clazz);
            }
            return template.findById(id, clazz);
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(List, Class)
     */
    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws UserInterfaceDalErrorException {
        return find2(tuples, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#find2(List, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> find2(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws UserInterfaceDalErrorException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(clazz);
            }
            Query query;
            Criteria cd = null;
            switch (tuples.size()) {
                case 0:
                    query = new Query();
                    break;
                case 1:
                    ConditionTuple tuple = tuples.get(0);
                    cd = where(tuple.field).is(tuple.value);
                default:
                    for (int index = 1; index < tuples.size(); index++) {
                        tuple = tuples.get(index);
                        cd.and(tuple.field).is(tuple.value);
                    }
                    query = query(cd);
                    break;
            }
            return template.find(query, clazz);
        } catch (ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Condition find entity[%s] fail, condition: %s.",
                        entityClass.getName(), StringUtils.merge(tuples, ",")), ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#findOne(List, Class)
     */
    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws UserInterfaceDalErrorException {
        return findOne2(tuples, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralEntityAccessor#findOne2(List, Class, boolean)
     */
    @Override
    public <T extends Base> T findOne2(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws UserInterfaceDalErrorException {
        List<T> list = find2(tuples, entityClass, isInterfaceClass);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(Base)
     */
    @Override
    public <T extends Base> T save(T t) throws UserInterfaceDalErrorException {
        return save(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(Base, boolean)
     */
    @Override
    public <T extends Base> T save(T t, boolean saveOperateLog) throws UserInterfaceDalErrorException {
        boolean isNew = false;
        if (StringUtils.isBlank(t.getId())) {
            // new
            t.setCreatedTime(new Date().getTime());
            isNew = true;
        } else {
            T old = this.getById(t.getId(), (Class<T>) t.getClass());
            if (old == null) {
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_NOT_FOUND);
            }
            t.setCreatedTime(old.getCreatedTime());
            // 修改操作不能修改代码字段
            if (t instanceof BaseDict) {
                ((BaseDict) t).setCode(((BaseDict) old).getCode());
            }
        }
        t.setUpdatedTime(new Date().getTime());
        t.setOperator(sessionDataStore.getCurrentUserCode());
        template.save(t);
        t = template.findById(t.getId(), (Class<T>) t.getClass());
        if (saveOperateLog) {
            writeOperateLog(t, String.format("%s了%s实体[%s]。",
                    isNew ? "新增" : "修改", t.getClass().getSimpleName(), t.getId()));
        }
        return t;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class)
     */
    @Override
    public <T extends Base> T remove(String id, Class<T> entityInterfaceClass) throws UserInterfaceDalErrorException {
        return remove(id, entityInterfaceClass, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class, boolean)
     */
    @Override
    public <T extends Base> T remove(String id, Class<T> entityInterfaceClass, boolean logicRemove)
            throws UserInterfaceDalErrorException {
        T t = getById(id, entityInterfaceClass);
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
    @Override
    public <T extends Base> T remove(T t) throws UserInterfaceDalErrorException {
        return remove(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base, boolean)
     */
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) throws UserInterfaceDalErrorException {
        if (logicRemove) {
            // logically remove
            t.setValid(false);
            t = save(t);
            writeOperateLog(t, String.format("逻辑删除了%s实体[%s]。", t.getClass().getSimpleName(), t.getId()));
            return t;
        } else {
            // physically remove
            template.remove(t);
            writeOperateLog(t, String.format("物理删除了%s实体[%s]。", t.getClass().getSimpleName(), t.getId()));
            return t;
        }
    }

    private <T> void writeOperateLog(T t, String content) throws UserInterfaceDalErrorException {
        if (operateLogService == null || t instanceof OperateLog) {
            return;
        }
        operateLogService.writeLog(content);
    }
}
