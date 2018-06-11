package org.mx.dal.service.impl;

import com.alibaba.fastjson.JSON;
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
    private ElasticUtil accessor;

    public GeneralAccessorElasticImpl() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param elasticUtil ES工具
     */
    public GeneralAccessorElasticImpl(ElasticUtil elasticUtil) {
        this();
        this.accessor = elasticUtil;
    }

    private List<GeneralAccessor.ConditionTuple> validateCondition(boolean isValid) {
        List<GeneralAccessor.ConditionTuple> list = null;
        if (isValid) {
            list = Collections.singletonList(new GeneralAccessor.ConditionTuple("valid", true));
        }
        return list;
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
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException {
        SearchResponse response = accessor.search(validateCondition(isValid), clazz, null);
        return response.getHits().getTotalHits();
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> clazz) throws UserInterfaceDalErrorException {
        return list(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException {
        SearchResponse response = accessor.search(validateCondition(isValid), clazz, null);
        List<T> list = new ArrayList<>();
        response.getHits().forEach(hit -> dowithRow(hit, list));
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz) throws UserInterfaceDalErrorException {
        return list(pagination, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException {
        if (pagination == null) {
            pagination = new Pagination();
        }
        SearchResponse response = accessor.search(validateCondition(isValid), clazz, pagination);
        if (response.status() == RestStatus.OK) {
            List<T> list = new ArrayList<>();
            pagination.setTotal((int) response.getHits().getTotalHits());
            response.getHits().forEach(hit -> dowithRow(hit, list));
            return list;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) throws UserInterfaceDalErrorException {
        return accessor.getById(id, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(List, Class)
     */
    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> clazz)
            throws UserInterfaceDalErrorException {
        return find(tuples, Collections.singletonList(clazz));
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#find(List, List)
     */
    @Override
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, List<Class<? extends Base>> classes)
            throws UserInterfaceDalErrorException {
        SearchResponse response = accessor.search(tuples, classes, null);
        List<T> list = new ArrayList<>();
        response.getHits().forEach(hit -> dowithRow(hit, list));
        return list;
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> void dowithRow(SearchHit hit, List<T> list) {
        T t = JSON.parseObject(hit.getSourceAsString(), (Class<T>) accessor.getIndexClass(hit.getIndex()));
        ((ElasticBaseEntity)t).setScore(hit.getScore());
        list.add(t);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#findOne(List, Class)
     */
    @Override
    public <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> clazz) throws UserInterfaceDalErrorException {
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
    @Override
    public <T extends Base> T save(T t) throws UserInterfaceDalErrorException {
        return accessor.index(t);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class)
     */
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz) throws UserInterfaceDalErrorException {
        return remove(id, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class, boolean)
     */
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz, boolean logicRemove) throws UserInterfaceDalErrorException {
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
        return accessor.remove(t, logicRemove);
    }
}
