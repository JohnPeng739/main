package org.mx.dal.service.impl;

import com.mongodb.util.JSONParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.entity.BaseDictTree;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralTextSearchAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoWriter;
import org.springframework.data.mongodb.core.query.*;

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
public class GeneralAccessorMongoImpl implements GeneralAccessor, GeneralTextSearchAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorMongoImpl.class);
    private static final String ID_FIELD = "_id";

    protected MongoTemplate template;
    private SessionDataStore sessionDataStore;

    public GeneralAccessorMongoImpl(MongoTemplate template, SessionDataStore sessionDataStore) {
        super();
        this.template = template;
        this.sessionDataStore = sessionDataStore;
    }

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
    public <T extends Base> long count(Class<T> clazz) {
        return count(clazz, true);
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> T fetchParentMaybe(T t) {
        if (t instanceof BaseDictTree) {
            String parentId = ((BaseDictTree) t).getParentId();
            if (!StringUtils.isBlank(parentId)) {
                T parent = template.findById(parentId, (Class<T>) t.getClass());
                ((BaseDictTree) t).setParent((BaseDictTree) parent);
            }
        }
        return t;
    }

    private <T extends Base> List<T> fetchParentMaybe(List<T> list) {
        if (list != null && !list.isEmpty()) {
            for (T t : list) {
                fetchParentMaybe(t);
            }
        }
        return list;
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
                tuples.add(ConditionTuple.eq("valid", true));
                ConditionGroup group = ConditionGroup.and();
                tuples.forEach(group::add);
                result = find(group, clazz);
            } else {
                result = fetchParentMaybe(template.findAll(clazz));
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
            List<T> result;
            if (isValid) {
                result = template.find(query(where("valid").is(true)).skip(skip).limit(limit), clazz);
            } else {
                result = template.find(new Query().skip(skip).limit(limit), clazz);
            }
            return fetchParentMaybe(result);
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
            return fetchParentMaybe(template.findById(id, clazz));
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    private Criteria createToupleCriteria(ConditionTuple tuple) {
        switch (tuple.operate) {
            case CONTAIN:
                // 将关键字中的空格转化为正则表达式的 OR 操作\
                return where(tuple.field).regex(String.format(".*(%s).*",
                        ((String) tuple.value).replaceAll(" ", " | ")));
            case PREFIX:
                // 将关键字中的空格转化为正则表达式的 OR 操作
                return where(tuple.field).regex(String.format("(%s).*",
                        ((String) tuple.value).replaceAll(" ", " | ")));
            case EQ:
                return where(tuple.field).is(tuple.value);
            case LT:
                return where(tuple.field).lt(tuple.value);
            case GT:
                return where(tuple.field).gt(tuple.value);
            case LTE:
                return where(tuple.field).lte(tuple.value);
            case GTE:
                return where(tuple.field).gte(tuple.value);
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported the operate type: %s.", tuple.operate));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
        }
    }

    private Criteria createGroupCriteria(ConditionGroup group) {
        Criteria criteria = new Criteria();
        if (group.getItems().size() == 1) {
            return createToupleCriteria((ConditionTuple) group.getItems().get(0));
        }
        Criteria[] subCriterias = new Criteria[group.getItems().size()];
        for (int index = 0; index < group.getItems().size(); index++) {
            subCriterias[index] = createGroupCriteria(group.getItems().get(index));

        }
        if (group.getOperateType() == ConditionGroup.OperateType.AND) {
            criteria.andOperator(subCriterias);
        } else {
            criteria.orOperator(subCriterias);
        }
        return criteria;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(ConditionGroup, Class)
     */
    @Override
    public <T extends Base> List<T> find(ConditionGroup group, Class<T> clazz) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            Query query = new Query();
            if (group != null && !group.getItems().isEmpty()) {
                query.addCriteria(createGroupCriteria(group));
            }
            return fetchParentMaybe(template.find(query, clazz));
        } catch (ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Condition find entity[%s] fail, condition: %s.",
                        clazz.getName(), group), ex);
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
        ConditionGroup group = ConditionGroup.and();
        if (tuples != null && !tuples.isEmpty()) {
            tuples.forEach(group::add);
        }
        List<T> list = find(group, clazz);
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
            return fetchParentMaybe(template.find(query, clazz));
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
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> T save(T t) {
        if (StringUtils.isBlank(t.getId())) {
            // new
            t.setCreatedTime(new Date().getTime());
        } else {
            T old = this.getById(t.getId(), (Class<T>) t.getClass());
            if (old != null) {
                t.setCreatedTime(old.getCreatedTime());
                // 修改操作不能修改代码字段
                if (t instanceof BaseDict) {
                    ((BaseDict) t).setCode(((BaseDict) old).getCode());
                }
            }
        }
        t.setUpdatedTime(new Date().getTime());
        t.setOperator(sessionDataStore.getCurrentUserCode());
        template.save(t);
        if (t instanceof BaseDictTree) {
            // 处理父级节点
            String parentId = ((BaseDictTree) t).getParentId();
            if (!StringUtils.isBlank(parentId)) {
                T parent = template.findById(parentId, (Class<T>) t.getClass());
                ((BaseDictTree) parent).getChildren().add(t);
                template.save(parent);
            }
        }
        t = template.findById(t.getId(), (Class<T>) t.getClass());
        return t;
    }

    private <T> Document toDocument(T objectToSave, MongoWriter<T> writer) {
        if (objectToSave instanceof Document) {
            return (Document) objectToSave;
        }

        if (!(objectToSave instanceof String)) {
            Document dbDoc = new Document();
            writer.write(objectToSave, dbDoc);

            if (dbDoc.containsKey(ID_FIELD) && dbDoc.get(ID_FIELD) == null) {
                dbDoc.remove(ID_FIELD);
            }
            return dbDoc;
        } else {
            try {
                return Document.parse((String) objectToSave);
            } catch (JSONParseException e) {
                throw new MappingException("Could not parse given String to save into a JSON document!", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(List)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> save(List<T> ts) {
        if (ts == null || ts.isEmpty()) {
            return ts;
        }
        BulkOperations bulk = template.bulkOps(BulkOperations.BulkMode.ORDERED, ts.get(0).getClass());
        for (T t : ts) {
            boolean isNew = true;
            if (StringUtils.isBlank(t.getId())) {
                // new
                t.setCreatedTime(new Date().getTime());
            } else {
                T old = this.getById(t.getId(), (Class<T>) t.getClass());
                if (old != null) {
                    isNew = false;
                    t.setCreatedTime(old.getCreatedTime());
                    // 修改操作不能修改代码字段
                    if (t instanceof BaseDict) {
                        ((BaseDict) t).setCode(((BaseDict) old).getCode());
                    }
                }
            }
            t.setUpdatedTime(new Date().getTime());
            t.setOperator(sessionDataStore.getCurrentUserCode());
            if (isNew) {
                bulk.insert(t);
            } else {
                bulk.updateOne(new Query(where("id").is(t.getId())),
                        new BasicUpdate(toDocument(t, template.getConverter())));
            }
            if (t instanceof BaseDictTree) {
                // 处理父级节点
                BaseDictTree p = ((BaseDictTree) t).getParent();
                if (p != null) {
                    p.getChildren().add(t);
                    bulk.updateOne(new Query(where("id").is(t.getId())),
                            new BasicUpdate(toDocument(p, template.getConverter())));
                }
            }
        }
        bulk.execute();
        return ts;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#clear(Class)
     */
    @Override
    public <T extends Base> void clear(Class<T> clazz) {
        if (template.collectionExists(clazz)) {
            template.dropCollection(clazz);
        }
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
            return t;
        } else {
            // physically remove
            template.remove(t);
            return t;
        }
    }
}
