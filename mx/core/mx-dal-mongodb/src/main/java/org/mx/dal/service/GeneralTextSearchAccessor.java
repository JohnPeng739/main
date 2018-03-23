package org.mx.dal.service;

import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;

import java.util.List;

/**
 * 描述： 全文检索的访问接口定义
 *
 * @author John.Peng
 *         Date time 2018/3/23 上午8:51
 */
public interface GeneralTextSearchAccessor {
    /**
     * @param content 检索内容
     * @param valid   如果设置为true，表示仅搜索有效记录
     * @param clazz   数据实体或接口定义类
     * @param <T>     数据实体范型定义
     * @return 符合条件的记录列表
     */
    <T extends Base> List<T> search(String content, boolean valid, Class<T> clazz);

    /**
     * @param contents 检索内容
     * @param valid    如果设置为true，表示仅搜索有效记录
     * @param clazz    数据实体或接口定义类
     * @param <T>      数据实体范型定义
     * @return 符合条件的记录列表
     */
    <T extends Base> List<T> search(List<String> contents, boolean valid, Class<T> clazz);

    /**
     * @param pagination 分页对象
     * @param content    检索内容
     * @param valid      如果设置为true，表示仅搜索有效记录
     * @param clazz      数据实体或接口定义类
     * @param <T>        数据实体范型定义
     * @return 符合条件的记录列表
     */
    <T extends Base> List<T> search(Pagination pagination, String content, boolean valid, Class<T> clazz);

    /**
     * @param pagination 分页对象
     * @param contents   检索内容集合，条件用and
     * @param valid      如果设置为true，表示仅搜索有效记录
     * @param clazz      数据实体或接口定义类
     * @param <T>        数据实体范型定义
     * @return 符合条件的记录列表
     */
    <T extends Base> List<T> search(Pagination pagination, List<String> contents, boolean valid, Class<T> clazz);
}
