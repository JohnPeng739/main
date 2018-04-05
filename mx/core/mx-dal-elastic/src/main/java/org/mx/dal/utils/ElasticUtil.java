package org.mx.dal.utils;

import org.elasticsearch.action.search.SearchResponse;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;

import java.util.List;

/**
 * 描述：访问Elastic的接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/1 上午9:32
 */
public interface ElasticUtil {
    /**
     * 获取指定索引对应的实体类名
     *
     * @param index 索引名
     * @return 实体类名
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     */
    Class<?> getIndexClass(String index) throws UserInterfaceDalErrorException;

    /**
     * 根据输入的条件进行检索
     *
     * @param tuples     条件集合
     * @param clazz      对应的实体类，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param pagination 分页对象
     * @param <T>        范型定义
     * @return 查询响应对象
     * @throws UserInterfaceDalErrorException 查询过程中发生的异常
     */
    <T extends Base> SearchResponse search(List<GeneralAccessor.ConditionTuple> tuples, Class<T> clazz,
                                           Pagination pagination) throws UserInterfaceDalErrorException;

    /**
     * 根据输入的条件进行检索
     *
     * @param tuples     条件集合
     * @param classes    对应的实体类集合，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param pagination 分页对象
     * @return 查询响应对象
     * @throws UserInterfaceDalErrorException 查询过程中发生的异常
     */
    SearchResponse search(List<GeneralAccessor.ConditionTuple> tuples, List<Class<? extends Base>> classes,
                                           Pagination pagination)
            throws UserInterfaceDalErrorException;

    /**
     * 根据指定的ID获取实体对象
     *
     * @param id    关键字ID
     * @param clazz 实体类，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param <T>   范型定义
     * @return 如果不存在，则返回null。
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     */
    <T extends Base> T getById(String id, Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 索引指定的实体对象
     *
     * @param t   实体对象，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param <T> 范型定义
     * @return 索引成功后的实体对象
     * @throws UserInterfaceDalErrorException
     */
    <T extends Base> T index(T t) throws UserInterfaceDalErrorException;

    /**
     * 删除指定实体对应的索引信息
     *
     * @param t           实体对象，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param logicRemove 设置为true表示逻辑删除，否则物理删除
     * @param <T>         范型定义
     * @return 删除成功后的实体对象
     * @throws UserInterfaceDalErrorException 删除过程中发生的异常
     */
    <T extends Base> T remove(T t, boolean logicRemove) throws UserInterfaceDalErrorException;
}
