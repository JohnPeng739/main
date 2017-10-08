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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by john on 2017/10/8.
 */
@Component("generalEntityAccessorMongodb")
public class GeneralEntityAccessorImpl implements GeneralEntityAccessor {
    private static final Log logger = LogFactory.getLog(GeneralEntityAccessorImpl.class);

    @Autowired
    protected MongoTemplate template = null;

    @Autowired
    @Qualifier("sessionDataThreadLocal")
    private SessionDataStore sessionDataStore = null;

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
                clazz = getEntityClass(clazz);
            }
            long count = template.count(new Query(), clazz);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Count %d entity[%s].", count, entityClass.getName()));
            }
            return count;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Count entity[%s] fail.", entityClass.getName()), ex);
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
                clazz = getEntityClass(clazz);
            }
            List<T> result = template.findAll(clazz);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("List %d entity[%s].", result.size(), entityClass.getName()));
            }
            return result;
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("List entity[%s] fail.", entityClass.getName()), ex);
        }
    }

    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass) throws EntityAccessException {
        return list(pagination, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(clazz);
            }
            pagination.setTotal((int) count(clazz, false));
            int skip = (pagination.getPage() - 1) * pagination.getSize();
            int limit = pagination.getSize();
            return template.find(new Query().skip(skip).limit(limit), entityClass);
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Pagination list entity[%s] fail, page: %s.",
                    entityClass.getName(), pagination), ex);
        }
    }

    @Override
    public <T extends Base> T getById(String id, Class<T> entityInterfaceClass) throws EntityAccessException {
        return getById(id, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> T getById(String id, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        try {
            Class<T> clazz = entityClass;
            if (isInterfaceClass) {
                clazz = getEntityClass(clazz);
            }
            return template.findById(id, clazz);
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Get entity[%s] by id[%s] fail.",
                    entityClass.getName(), id), ex);
        }
    }

    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityInterfaceClass) throws EntityAccessException {
        return find(tuples, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
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
                    for (int index = 1; index < tuples.size(); index ++) {
                        tuple = tuples.get(index);
                        cd.and(tuple.field).is(tuple.value);
                    }
                    query = query(cd);
                    break;
            }
            return template.find(query, clazz);
        } catch (ClassNotFoundException ex) {
            throw new EntityAccessException(String.format("Condition find entity[%s] fail, condition: %s.",
                    entityClass.getName(), StringUtils.merge(tuples, ",")), ex);
        }
    }

    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityInterfaceClass) throws EntityAccessException {
        return findOne(tuples, entityInterfaceClass, true);
    }

    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException {
        List<T> list = find(tuples, entityClass, isInterfaceClass);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public <T extends Base> T save(T t) throws EntityAccessException {
        if (StringUtils.isBlank(t.getId())) {
            // new
            t.setCreatedTime(new Date().getTime());
        } else {
            T old = this.getById(t.getId(), (Class<T>) t.getClass());
            if (old == null) {
                throw new EntityAccessException(String.format("The entity[%s] not found.", t.getId()));
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
        return template.findById(t.getId(), (Class<T>) t.getClass());
    }

    @Override
    public <T extends Base> T remove(T t) throws EntityAccessException {
        return remove(t, true);
    }

    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) throws EntityAccessException {
        if (logicRemove) {
            // logically remove
            t.setValid(false);
            return save(t);
        } else {
            // physically remove
            template.remove(t);
            return t;
        }
    }
}
