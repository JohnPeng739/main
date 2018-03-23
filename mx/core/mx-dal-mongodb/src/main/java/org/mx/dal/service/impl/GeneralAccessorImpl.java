package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralTextSearchAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 基于Mongodb实现的基础实体访问实现类
 *
 * @author : john.peng date : 2017/10/8
 * @see GeneralAccessor
 * @see GeneralTextSearchAccessor
 */
@Component("generalAccessorMongodb")
public class GeneralAccessorImpl implements GeneralAccessor, GeneralTextSearchAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorImpl.class);

    @Autowired
    protected MongoTemplate template = null;

    @Autowired
    @Qualifier("sessionDataThreadLocal")
    private SessionDataStore sessionDataStore = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            long count;
            if (isValid) {
                count = template.count(query(where("valid").is(true)), clazz);
            } else {
                count = template.count(new Query(), clazz);
            }
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
     * @see GeneralAccessor#count(Class)
     */
    @Override
    public <T extends Base> long count(Class<T> clazz) throws UserInterfaceDalErrorException {
        return count(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> clazz, boolean isValid) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            List<T> result;
            if (isValid) {
                List<ConditionTuple> tuples = new ArrayList<>();
                tuples.add(new ConditionTuple("valid", true));
                result = find(tuples, clazz);
            } else {
                result = template.findAll(clazz);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("List %d entity[%s].", result.size(), clazz.getName()));
            }
            return result;
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
    public <T extends Base> List<T> list(Class<T> clazz) {
        return list(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz) {
        return list(pagination, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            pagination.setTotal((int) count(clazz, isValid));
            int skip = (pagination.getPage() - 1) * pagination.getSize();
            int limit = pagination.getSize();
            if (isValid) {
                return template.find(query(where("valid").is(true)).skip(skip).limit(limit), clazz);
            } else {
                return template.find(new Query().skip(skip).limit(limit), clazz);
            }
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
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
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> clazz)
            throws UserInterfaceDalErrorException {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            Query query;
            Criteria cd;
            switch (tuples.size()) {
                case 0:
                    query = new Query();
                    break;
                case 1:
                    ConditionTuple tuple = tuples.get(0);
                    cd = where(tuple.field).is(tuple.value);
                    query = query(cd);
                    break;
                default:
                    cd = where(tuples.get(0).field).is(tuples.get(0).value);
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
                        clazz.getName(), StringUtils.merge(tuples, ",")), ex);
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
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> clazz) {
        List<T> list = find(tuples, clazz);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTextSearchAccessor#search(String, boolean, Class)
     */
    @Override
    public <T extends Base> List<T> search(String content, boolean valid, Class<T> clazz) {
        return search(Collections.singletonList(content), valid, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTextSearchAccessor#search(List, boolean, Class)
     */
    @Override
    public <T extends Base> List<T> search(List<String> contents, boolean valid, Class<T> clazz) {
        return search(null, contents, valid, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTextSearchAccessor#search(Pagination, String, boolean, Class)
     */
    @Override
    public <T extends Base> List<T> search(Pagination pagination, String content, boolean valid, Class<T> clazz) {
        return search(pagination, Collections.singletonList(content), valid, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTextSearchAccessor#search(Pagination, List, boolean, Class)
     */
    @Override
    public <T extends Base> List<T> search(Pagination pagination, List<String> contents, boolean valid, Class<T> clazz) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            Query query = new TextQuery(new TextCriteria().matchingAny(contents.toArray(new String[0])));
            if (valid) {
                query.addCriteria(where("valid").is(true));
            }
            if (pagination != null) {
                pagination.setTotal((int) template.count(query, clazz));
                query.skip((pagination.getPage() - 1) * pagination.getSize());
                query.limit(pagination.getSize());
            }
            return template.find(query, clazz);
        } catch (ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Text search entity[%s] fail, condition: %s.",
                        clazz.getName(), StringUtils.merge(contents, ",")), ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(Base)
     */
    @Override
    public <T extends Base> T save(T t) {
        return save(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(Base, boolean)
     */
    @Override
    public <T extends Base> T save(T t, boolean saveOperateLog) {
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
    public <T extends Base> T remove(String id, Class<T> clazz) {
        return remove(id, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class, boolean)
     */
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
    @Override
    public <T extends Base> T remove(T t) {
        return remove(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base, boolean)
     */
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) {
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

    private <T> void writeOperateLog(T t, String content) {
        if (operateLogService == null || t instanceof OperateLog) {
            return;
        }
        operateLogService.writeLog(content);
    }
}
