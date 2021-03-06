package org.mx.dal.service.impl;

import com.mongodb.util.JSONParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDictTree;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.AbstractGeneralAccessor;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralTextSearchAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.spring.session.SessionDataStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoWriter;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 基于Mongodb实现的基础实体访问实现类
 *
 * @author : john.peng date : 2017/10/8
 * @see GeneralAccessor
 * @see GeneralTextSearchAccessor
 */
public class GeneralAccessorMongoImpl extends AbstractGeneralAccessor implements GeneralAccessor, GeneralTextSearchAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorMongoImpl.class);
    private static final String ID_FIELD = "_id";

    protected MongoTemplate template;

    public GeneralAccessorMongoImpl(MongoTemplate template, SessionDataStore sessionDataStore) {
        super();
        this.template = template;
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
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The class[%s] not found.", clazz.getName()));
                }
                throw new UserInterfaceDalErrorException(
                        UserInterfaceDalErrorException.DalErrors.ENTITY_NOT_FOUND
                );
            }
        }
        Query query = new Query();
        if (group != null && !group.getItems().isEmpty()) {
            query.addCriteria(createGroupCriteria(group));
        }
        return template.count(query, clazz);
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
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> list(Class<T> clazz) {
        return list(clazz, true);
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
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Transactional(readOnly = true)
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
            return result;
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
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
            return template.findById(id, clazz);
        } catch (ClassNotFoundException ex) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    private Criteria createToupleCriteria(ConditionTuple tuple) {
        switch (tuple.operate) {
            case CONTAIN:
                // 将关键字中的空格转化为正则表达式的 OR 操作
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
            case IS_NULL:
                return where(tuple.field).exists(false);
            case IS_NOT_NULL:
                return where(tuple.field).exists(true);
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

    private Query withSortOrder(Query query, RecordOrderGroup orderGroup) {
        if (orderGroup != null && !orderGroup.getOrders().isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            orderGroup.getOrders().forEach(order -> {
                if (order.getType() == RecordOrder.OrderType.ASC) {
                    orders.add(Sort.Order.asc(order.getField()));
                } else {
                    orders.add(Sort.Order.desc(order.getField()));
                }
            });
            query.with(Sort.by(orders));
        }
        return query;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(ConditionGroup, Class)
     */
    @Transactional(readOnly = true)
    @Override
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
    public <T extends Base> List<T> find(Pagination pagination, ConditionGroup group, RecordOrderGroup orderGroup, Class<T> clazz) {
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            Query query = new Query();
            if (group != null && !group.getItems().isEmpty()) {
                query.addCriteria(createGroupCriteria(group));
            }
            if (pagination != null) {
                pagination.setTotal((int) template.count(query, clazz));
                query.skip((pagination.getPage() - 1) * pagination.getSize());
                query.limit(pagination.getSize());
            }
            query = withSortOrder(query, orderGroup);
            return template.find(query, clazz);
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
     * @see GeneralAccessor#findOne(ConditionGroup, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> T findOne(ConditionGroup group, Class<T> clazz) {
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
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> search(String content, boolean valid, Class<T> clazz) {
        return search(Collections.singletonList(content), valid, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTextSearchAccessor#search(List, boolean, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> search(List<String> contents, boolean valid, Class<T> clazz) {
        return search(null, contents, valid, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTextSearchAccessor#search(Pagination, String, boolean, Class)
     */
    @Transactional(readOnly = true)
    @Override
    public <T extends Base> List<T> search(Pagination pagination, String content, boolean valid, Class<T> clazz) {
        return search(pagination, Collections.singletonList(content), valid, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTextSearchAccessor#search(Pagination, List, boolean, Class)
     */
    @Transactional(readOnly = true)
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
    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> T save(T t) {
        super.prepareSave(t);
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

    private <T> Update toUpdate(T objectToSave, MongoWriter<T> writer) {
        Document doc = null;
        if (objectToSave instanceof Document) {
            doc = (Document) objectToSave;
        }

        if (!(objectToSave instanceof String)) {
            Document dbDoc = new Document();
            writer.write(objectToSave, dbDoc);

            if (dbDoc.containsKey(ID_FIELD) && dbDoc.get(ID_FIELD) == null) {
                dbDoc.remove(ID_FIELD);
            }
            doc = dbDoc;
        } else {
            try {
                doc = Document.parse((String) objectToSave);
            } catch (JSONParseException e) {
                throw new MappingException("Could not parse given String to save into a JSON document!", e);
            }
        }

        Set<String> excludes = new HashSet<>(Arrays.asList("_id", "_class"));
        Update update = new Update();
        for (String key : doc.keySet()) {
            if (excludes.contains(key)) {
                continue;
            }
            Object value = doc.get(key);
            update.set(key, value);
        }
        return update;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(List)
     */
    @Transactional
    @Override
    public <T extends Base> List<T> save(List<T> ts) {
        return save(ts, false);
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> List<T> save(List<T> ts, boolean logicRemove) {
        if (ts == null || ts.isEmpty()) {
            return ts;
        }
        BulkOperations bulk = template.bulkOps(BulkOperations.BulkMode.ORDERED, ts.get(0).getClass());
        for (T t : ts) {
            if (logicRemove) {
                t.setValid(false);
            }
            T old = prepareSave(t);
            if (old == null) {
                bulk.insert(t);
            } else {
                bulk.updateOne(new Query(where("_id").is(t.getId())), toUpdate(t, template.getConverter()));
            }
            if (t instanceof BaseDictTree) {
                // 处理父级节点
                BaseDictTree p = ((BaseDictTree) t).getParent();
                if (p != null) {
                    p.getChildren().add(t);
                    bulk.updateOne(new Query(where("_id").is(t.getId())),
                            toUpdate(p, template.getConverter()));
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
    @Transactional
    @Override
    public <T extends Base> void clear(Class<T> clazz) {
        template.remove(clazz).all();
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
    @Transactional
    @Override
    public <T extends Base> T remove(T t) {
        return remove(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base, boolean)
     */
    @Transactional
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

    @Transactional
    @Override
    public <T extends Base> List<T> remove(List<T> ts, boolean logicRemove) {
        if (ts == null || ts.isEmpty()) {
            return ts;
        }
        if (logicRemove) {
            return save(ts, true);
        } else {
            BulkOperations bulk = template.bulkOps(BulkOperations.BulkMode.ORDERED, ts.get(0).getClass());
            for (T t : ts) {
                bulk.remove(new Query(where("_id").is(t.getId())));
            }
            bulk.execute();
        }
        return ts;
    }
}
