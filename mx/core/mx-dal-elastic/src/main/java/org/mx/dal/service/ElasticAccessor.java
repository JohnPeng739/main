package org.mx.dal.service;

import org.mx.dal.entity.Base;
import org.mx.dal.error.UserInterfaceDalErrorException;

import java.util.List;

/**
 * 描述： 基于Elastic的多Indices搜索接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/5 上午11:08
 */
public interface ElasticAccessor {
    /**
     * 在指定的多个索引中搜索指定条件的实体
     *
     * @param tuples  条件集合
     * @param classes 实体类集合
     * @param <T>     范型定义
     * @return 符合条件的实体集合
     * @throws UserInterfaceDalErrorException 搜索过程中发生的异常
     */
    <T extends Base> List<T> find(List<GeneralAccessor.ConditionTuple> tuples, List<Class<? extends Base>> classes)
            throws UserInterfaceDalErrorException;
}
