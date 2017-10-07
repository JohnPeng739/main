package org.mx.dal.service;


import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.exception.EntityAccessException;

import java.util.List;

/**
 * 通用的实体存取接口定义
 * <p>
 * Created by john on 2017/8/18.
 */
public interface GeneralEntityAccessor {
    /**
     * 对指定的实体类型进行计数，实体接口必须继承Base接口。
     *
     * @param entityInterfaceClass 实体接口类型
     * @param <T>                  实现Base接口的泛型对象类型
     * @return 指定实体的数量
     * @throws EntityAccessException 计数过程中发生的异常
     */
    <T extends Base> long count(Class<T> entityInterfaceClass) throws EntityAccessException;

    /**
     * 获取指定实体类型的数据集合，实体接口必须继承Base接口。
     *
     * @param entityInterfaceClass 实体接口类型
     * @param <T>                  实现Base接口的泛型对象类型
     * @return 指定实体对象类别
     * @throws EntityAccessException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Class<T> entityInterfaceClass) throws EntityAccessException;

    /**
     * 根据分页信息获取指定实体类型的数据子集合，实体接口必须继承Base接口。
     *
     * @param pagination           分页信息
     * @param entityInterfaceClass 实体接口类型
     * @param <T>                  实现Base接口的泛型对象类型
     * @return 指定实体对象集合
     * @throws EntityAccessException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Pagination pagination, Class<T> entityInterfaceClass) throws EntityAccessException;

    /**
     * 根据实体ID和实体接口类型获取实体，实体接口必须继承Base接口。
     *
     * @param id                   实体ID
     * @param entityInterfaceClass 实体接口类
     * @param <T>                  实现Base接口的泛型对象类型
     * @return 实体，如果实体不存在，则返回null。
     * @throws EntityAccessException 获取实体过程中发生的异常
     */
    <T extends Base> T getById(String id, Class<T> entityInterfaceClass) throws EntityAccessException;

    /**
     * 根据指定字段的值获取数据记录集合，多个条件采用and组合。
     *
     * @param tuples                条件元组（包括字段名和字段值）
     * @param entityInterfaceClass 实体接口类
     * @param <T>                  实现Base接口的泛型对象类型
     * @return 实体对象集合
     * @throws EntityAccessException 获取过程中发生的异常
     * @see ConditionTuple
     */
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws EntityAccessException;

    /**
     * 根据指定字段的值获取一条数据记录，多个条件采用and组合。
     *
     * @param tuples                条件元组（包括字段名和字段值）
     * @param entityInterfaceClass 实体接口类
     * @param <T>                  实现Base接口的泛型对象类型
     * @return 实体对象，如果不存在则返回null
     * @throws EntityAccessException 获取过程中发生的异常
     * @see ConditionTuple
     * @see #find(List, Class)
     */
    <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> entityInterfaceClass)
            throws EntityAccessException;

    /**
     * 保存指定的实体
     *
     * @param t   实体对象
     * @param <T> 实现Base接口的泛型对象类型
     * @return 返回保存后的实体对象
     * @throws EntityAccessException 保存实体过程中发生的异常
     */
    <T extends Base> T save(T t) throws EntityAccessException;

    /**
     * 逻辑删除指定的实体
     *
     * @param t   实体对象
     * @param <T> 实现Base接口的泛型对象类型
     * @return 删除的实体
     * @throws EntityAccessException 删除过程中发生的异常
     * @see #remove(Base, boolean)
     */
    <T extends Base> T remove(T t) throws EntityAccessException;

    /**
     * 删除指定的实体，支持逻辑删除和物理删除。
     *
     * @param t           实体对象
     * @param <T>         实现Base接口的泛型对象类型
     * @param logicRemove 设置为true表示逻辑删除，否则物理删除。
     * @return 删除的实体
     * @throws EntityAccessException 删除过程中发生的异常
     */
    <T extends Base> T remove(T t, boolean logicRemove) throws EntityAccessException;

    class ConditionTuple {
        public String field;
        public Object value;

        public ConditionTuple() {
            super();
        }

        public ConditionTuple(String field, Object value) {
            this();
            this.field = field;
            this.value = value;
        }
    }
}
