package org.mx.spring.session;

import java.util.Map;

/**
 * 会话数据访问接口定义，用于存放线程安全的会话信息。
 *
 * @author : john.peng date : 2017/8/18
 */
public interface SessionDataStore {
    /**
     * 获取当前会话中关联的相关数据
     *
     * @return 数据集合
     */
    Map<String, Object> get();

    /**
     * 向当前会话关联的数据集合中添加键值对集合
     *
     * @param map 待添加的键值对集合
     * @return 添加后的集合
     */
    Map<String, Object> set(Map<String, Object> map);

    /**
     * 向当前会话关联的数据集合中添加键值对
     *
     * @param key   键
     * @param value 值
     * @return 添加后的集合
     */
    Map<String, Object> set(String key, Object value);

    /**
     * 在当前会话关联的数据集合中删除指定的键
     *
     * @param key 键
     * @return 删除后的集合
     */
    Map<String, Object> remove(String key);

    /**
     * 获取当前会话关联的操作者代码
     *
     * @return 操作者代码
     */
    String getCurrentUserCode();

    /**
     * 设置当前会话关联的操作者代码
     *
     * @param userCode 操作者代码
     */
    void setCurrentUserCode(String userCode);

    /**
     * 清除当前会话关联的操作者代码
     */
    void removeCurrentUserCode();

    /**
     * 获取当前会话关联的系统
     *
     * @return 系统
     */
    String getCurrentSystem();

    /**
     * 设置当前会话关联的系统
     *
     * @param system 系统
     */
    void setCurrentSystem(String system);

    /**
     * 获取当前会话关联的模块
     *
     * @return 模块
     */
    String getCurrentModule();

    /**
     * 设置当前会话关联的模块
     *
     * @param module 模块
     */
    void setCurrentModule(String module);

    /**
     * 清除当前会话中关联的所有数据
     */
    void clean();
}
