package org.mx.dal.service;

import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.exception.EntityAccessException;

import java.util.List;

/**
 * 基于Mongodb实现的基础实体访问接口定义
 *
 * @author : john.peng date : 2017/10/7
 * @see GeneralAccessor
 */
public interface GeneralEntityAccessor extends GeneralAccessor {
    /**
     * 对指定的实体类型进行计数，实体接口必须继承Base接口。
     *
     * @param entityClass      实体类，也可能是实体接口类
     * @param <T>              实现Base接口的泛型对象类型
     * @param isInterfaceClass 如果为true，表示entityClass指定的类为实体接口类，否则表示真实的实体类
     * @return 指定实体的数量
     * @throws EntityAccessException 计数过程中发生的异常
     */
    <T extends Base> long count(Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException;

    /**
     * 获取指定实体类型的数据集合，实体接口必须继承Base接口。
     *
     * @param entityClass      实体类，也可能是实体接口类
     * @param <T>              实现Base接口的泛型对象类型
     * @param isInterfaceClass 如果为true，表示entityClass指定的类为实体接口类，否则表示真实的实体类
     * @return 指定实体对象类别
     * @throws EntityAccessException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException;

    /**
     * 根据分页信息获取指定实体类型的数据子集合，实体接口必须继承Base接口。
     *
     * @param pagination       分页信息
     * @param entityClass      实体类，也可能是实体接口类
     * @param <T>              实现Base接口的泛型对象类型
     * @param isInterfaceClass 如果为true，表示entityClass指定的类为实体接口类，否则表示真实的实体类
     * @return 指定实体对象集合
     * @throws EntityAccessException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Pagination pagination, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException;

    /**
     * 根据实体ID和实体接口类型获取实体，实体接口必须继承Base接口。
     *
     * @param id               实体ID
     * @param entityClass      实体类，也可能是实体接口类
     * @param <T>              实现Base接口的泛型对象类型
     * @param isInterfaceClass 如果为true，表示entityClass指定的类为实体接口类，否则表示真实的实体类
     * @return 实体，如果实体不存在，则返回null。
     * @throws EntityAccessException 获取实体过程中发生的异常
     */
    <T extends Base> T getById(String id, Class<T> entityClass, boolean isInterfaceClass) throws EntityAccessException;

    /**
     * 根据指定字段的值获取数据记录集合，多个条件采用and组合。
     *
     * @param tuples           条件元组（包括字段名和字段值）
     * @param entityClass      实体类，也可能是实体接口类
     * @param <T>              实现Base接口的泛型对象类型
     * @param isInterfaceClass 如果为true，表示entityClass指定的类为实体接口类，否则表示真实的实体类
     * @return 实体对象集合
     * @throws EntityAccessException 获取过程中发生的异常
     * @see ConditionTuple
     */
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException;

    /**
     * 根据指定字段的值获取一条数据记录，多个条件采用and组合。
     *
     * @param tuples           条件元组（包括字段名和字段值）
     * @param entityClass      实体类，也可能是实体接口类
     * @param <T>              实现Base接口的泛型对象类型
     * @param isInterfaceClass 如果为true，表示entityClass指定的类为实体接口类，否则表示真实的实体类
     * @return 实体对象，如果不存在则返回null
     * @throws EntityAccessException 获取过程中发生的异常
     * @see ConditionTuple
     * @see #find(List, Class)
     */
    <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityClass, boolean isInterfaceClass)
            throws EntityAccessException;
}
