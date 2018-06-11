package org.mx.dal.service.impl;

import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.utils.ElasticUtil;

import java.util.Collections;
import java.util.List;

/**
 * 描述： 基于Elastic实现的基础字典访问类
 *
 * @author John.Peng
 * Date time 2018/4/1 上午9:20
 */
public class GeneralDictAccessorElasticImpl extends GeneralAccessorElasticImpl implements GeneralDictAccessor {
    /**
     * 默认的构造函数
     *
     * @param elasticUtil ES访问工具
     */
    public GeneralDictAccessorElasticImpl(ElasticUtil elasticUtil) {
        super(elasticUtil);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralDictAccessor#getByCode(String, Class)
     */
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> clazz) throws UserInterfaceDalErrorException {
        List<ConditionTuple> tuples = Collections.singletonList(new ConditionTuple("code", code));
        List<T> list = super.find(tuples, clazz);
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }
}
