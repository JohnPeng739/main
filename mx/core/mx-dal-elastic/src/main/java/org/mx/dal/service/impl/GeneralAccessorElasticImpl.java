package org.mx.dal.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.ElasticBaseEntity;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.ElasticAccessor;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.utils.ElasticUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述： 基于Elastic实现的数据基础操作（CRUD）
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:56
 */
public class GeneralAccessorElasticImpl implements GeneralAccessor, ElasticAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorElasticImpl.class);

    private ElasticUtil elasticUtil;

    /**
     * 默认的构造函数
     *
     * @param elasticUtil ES工具
     */
    public GeneralAccessorElasticImpl(ElasticUtil elasticUtil) {
        super();
        this.elasticUtil = elasticUtil;
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

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) {
        SearchResponse response = elasticUtil.search(
                isValid ? ConditionGroup.and(ConditionTuple.eq("valid", true)) : null,
                clazz, null);
        return response.getHits().getTotalHits();
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
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> clazz, boolean isValid) {
        return list(null, clazz, isValid);
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
        return find(
                isValid ? ConditionGroup.and(ConditionTuple.eq("valid", true)) : null,
                Collections.singletonList(clazz), pagination);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) {
        return elasticUtil.getById(id, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(org.mx.dal.service.GeneralAccessor.ConditionGroup, Class)
     */
    @Override
    public <T extends Base> List<T> find(ConditionGroup group, Class<T> clazz) {
        return find(group, Collections.singletonList(clazz), null);
    }

    /**
     * 根据条件组进行分页查询
     *
     * @param group      条件组
     * @param classes    实体类定义列表
     * @param pagination 分页对象
     * @param <T>        实体泛型
     * @return 符合条件的实体列表
     */
    public <T extends Base> List<T> find(ConditionGroup group, List<Class<? extends Base>> classes, Pagination pagination) {
        SearchResponse response = elasticUtil.search(group, classes, pagination);
        List<T> list = new ArrayList<>();
        if (response.status() == RestStatus.OK) {
            response.getHits().forEach(hit -> dowithRow(hit, list));
        }
        return list;
    }

    /**
     * {@inheritDoc} <br>
     * 如果预计记录数超过1000，则推荐使用带分页条件的查询 {@link ElasticUtil#search(org.mx.dal.service.GeneralAccessor.ConditionGroup, Class, Pagination)}。否则只返回前1000条记录。
     *
     * @see ElasticAccessor#find(GeneralAccessor.ConditionGroup, List)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> find(ConditionGroup group, List<Class<? extends Base>> classes) {
        return find(group, classes, null);
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> void dowithRow(SearchHit hit, List<T> list) {
        T t = JSON.parseObject(hit.getSourceAsString(), (Class<T>) elasticUtil.getIndexClass(hit.getIndex()));
        ((ElasticBaseEntity) t).setScore(hit.getScore());
        list.add(t);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#findOne(List, Class)
     */
    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> clazz) {
        ConditionGroup group = ConditionGroup.and();
        for (ConditionTuple tuple : tuples) {
            group.add(tuple);
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
    @Override
    public <T extends Base> T save(T t) {
        return elasticUtil.index(t);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(List)
     */
    @Override
    public <T extends Base> List<T> save(List<T> ts) {
        return elasticUtil.index(ts);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#clear(Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> void clear(Class<T> clazz) {
        if (ElasticBaseEntity.class.isAssignableFrom(clazz)) {
            elasticUtil.deleteIndex(clazz);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The class[%s] not extends from ElasticBaseEntity.", clazz.getName()));
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INDEX_INVALID_BASE);
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
        return elasticUtil.remove(t, logicRemove);
    }
}
