package org.mx.dal.service.impl;

import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 描述： 基于Elastic实现的基础字典访问类
 *
 * @author John.Peng
 *         Date time 2018/4/1 上午9:20
 */
@Component("generalDictAccessorElastic")
public class GeneralDictAccessorImpl extends GeneralAccessorImpl implements GeneralDictAccessor {
    /**
     * {@inheritDoc}
     *
     * @see GeneralDictAccessor#getByCode(String, Class)
     */
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> clazz) throws UserInterfaceDalErrorException {
        List<ConditionTuple> tuples = Arrays.asList(new ConditionTuple("code", code));
        List<T> list = super.accessor.find(tuples, clazz);
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }
}
