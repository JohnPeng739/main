package org.mx.spring.session.impl;

import org.mx.spring.session.SessionDataStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述： 基于线程局部堆的会话信息存储数据对象
 *
 * @author john peng
 * Date time 2018/10/7 下午8:17
 */
public class SessionDataThreadLocal implements SessionDataStore {
    private ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#get()
     */
    @Override
    public Map<String, Object> get() {
        return threadLocal.get();
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#set(Map)
     */
    @Override
    public Map<String, Object> set(Map<String, Object> map) {
        Map<String, Object> old = get();
        old.putAll(map);
        threadLocal.set(old);
        return old;
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#set(String, Object)
     */
    @Override
    public Map<String, Object> set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        map.put(key, value);
        threadLocal.set(map);
        return get();
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#removeCurrentUserCode()
     */
    @Override
    public Map<String, Object> remove(String key) {
        Map<String, Object> map = threadLocal.get();
        map.remove(key);
        threadLocal.set(map);
        return get();
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#getCurrentUserCode()
     */
    @Override
    public String getCurrentUserCode() {
        return (String) threadLocal.get().get("currentUser");
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#setCurrentUserCode(String)
     */
    @Override
    public void setCurrentUserCode(String userCode) {
        Map<String, Object> map = threadLocal.get();
        map.put("currentUser", userCode);
        threadLocal.set(map);
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#removeCurrentUserCode()
     */
    @Override
    public void removeCurrentUserCode() {

        Map<String, Object> map = threadLocal.get();
        map.remove("currentUser");
        threadLocal.set(map);
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#getCurrentSystem()
     */
    @Override
    public String getCurrentSystem() {
        return (String) threadLocal.get().get("currentSystem");
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#setCurrentSystem(String)
     */
    @Override
    public void setCurrentSystem(String system) {
        Map<String, Object> map = threadLocal.get();
        map.put("currentSystem", system);
        threadLocal.set(map);
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#getCurrentModule()
     */
    @Override
    public String getCurrentModule() {
        return (String) threadLocal.get().get("currentModule");
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#setCurrentModule(String)
     */
    @Override
    public void setCurrentModule(String module) {
        Map<String, Object> map = threadLocal.get();
        map.put("currentModule", module);
        threadLocal.set(map);
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#clean()
     */
    @Override
    public void clean() {
        threadLocal.set(new ConcurrentHashMap<>());
    }
}
