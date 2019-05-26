package org.mx.dal.service;

import org.mx.dal.entity.Base;

import java.util.List;

/**
 * 描述： 基于Hibernate的JdbcTemplate实现的批处理操作接口定义
 *
 * @author john peng
 * Date time 2019/5/24 5:04 PM
 */
public interface JdbcBatchAccessor {
    /**
     * 批量插入数据
     *
     * @param insertFields 插入数据字段列表
     * @param entities     数据实体列表
     * @param <T>          实体泛型定义
     * @return 插入的数据行数
     */
    <T extends Base> int batchInsert(List<String> insertFields, List<T> entities);

    /**
     * 批量更新数据
     *
     * @param primaryKeyFields 关键字字段列表
     * @param updateFields     更新字段列表
     * @param entities         数据实体列表
     * @param <T>              实体泛型定义
     * @return 更新的数据行数
     */
    <T extends Base> int batchUpdate(List<String> primaryKeyFields, List<String> updateFields, List<T> entities);

    /**
     * 批量删除数据
     *
     * @param primaryKeyFields 关键字字段列表
     * @param entities         数据实体列表
     * @param <T>              实体泛型定义
     * @return 删除的数据行数
     */
    <T extends Base> int batchDelete(List<String> primaryKeyFields, List<T> entities);
}
