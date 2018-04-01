package org.mx.dal.utils;

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
public interface ElasticAccessor {
    /**
     * 计数符合条件的实体数量
     *
     * @param clazz   指定的类型
     * @param isValid 设置为true仅搜索有效的数据，否则搜索所有数据
     * @param <T>     范型定义
     * @return 实体总数
     * @throws UserInterfaceDalErrorException 计数过程中发生的异常
     */
    <T extends Base> long count(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException;

    /**
     * 查询符合条件的实体
     *
     * @param clazz   指定的列席
     * @param isValid 设置为true仅搜索有效的数据，否则搜索所有数据
     * @param <T>     范型定义
     * @return 符合条件的实体列表
     * @throws UserInterfaceDalErrorException 搜索过程中发生的异常
     */
    <T extends Base> List<T> list(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException;

    /**
     * 分页查询符合条件的实体
     *
     * @param pagination 分页对象
     * @param clazz      指定的类型
     * @param isValid    设置为true仅搜索有效的数据，否则搜索所有数据
     * @param <T>        范型定义
     * @return 符合条件的实体列表
     * @throws UserInterfaceDalErrorException 搜索过程中发生的异常
     */
    <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException;

    /**
     * 根据指定的ID查找实体对象
     *
     * @param id    ID
     * @param clazz 指定的类型
     * @param <T>   范型定义
     * @return 指定ID的实体，如果不存在，则返回null
     * @throws UserInterfaceDalErrorException 查找过程中发生的异常
     */
    <T extends Base> T getById(String id, Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 根据指定的and条件查找数据实体
     *
     * @param tuples 条件列表
     * @param clazz  指定的类型
     * @param <T>    范型定义
     * @return 符合条件的实体列表
     * @throws UserInterfaceDalErrorException 搜索过程中发生的异常
     */
    <T extends Base> List<T> find(List<GeneralAccessor.ConditionTuple> tuples, Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 保存指定的实体对象
     *
     * @param t   待保存的实体对象
     * @param <T> 范型
     * @return 保存成功的实体对象
     * @throws UserInterfaceDalErrorException 保存过程中发生的异常
     */
    <T extends Base> T save(T t) throws UserInterfaceDalErrorException;

    /**
     * 删除指定的实体对象
     *
     * @param t           待删除的实体对象
     * @param logicRemove 是否逻辑删除
     * @param <T>         范型定义
     * @return 成功删除后的实体对象
     * @throws UserInterfaceDalErrorException 删除过程中发生的异常
     */
    <T extends Base> T remove(T t, boolean logicRemove) throws UserInterfaceDalErrorException;
}
