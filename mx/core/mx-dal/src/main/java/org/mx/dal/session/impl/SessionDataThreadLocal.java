package org.mx.dal.session.impl;

import org.mx.StringUtils;
import org.mx.dal.session.SessionDataStore;
import org.springframework.stereotype.Component;

/**
 * 采用线程局部变量方式实现的会话数据保存实现。
 * 目前实现了在局部线程变量中存储了当前操作者代码。
 *
 * @author : john.peng date : 2017/9/10
 * @see SessionDataStore
 */
@Component("sessionDataThreadLocal")
public class SessionDataThreadLocal implements SessionDataStore {
    private static ThreadLocal<String> currentSystem = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "default";
        }
    };
    private static ThreadLocal<String> currentModule = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "default";
        }
    };
    private static ThreadLocal<String> currentUser = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "NA";
        }
    };

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#getCurrentUserCode()
     */
    @Override
    public String getCurrentUserCode() {
        String userCode = currentUser.get();
        return StringUtils.isBlank(userCode) ? "NA" : userCode;
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#setCurrentUserCode(String)
     */
    @Override
    public void setCurrentUserCode(String userCode) {
        if (!StringUtils.isBlank(userCode)) {
            currentUser.set(userCode);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#getCurrentSystem()
     */
    @Override
    public String getCurrentSystem() {
        String system = currentSystem.get();
        return StringUtils.isBlank(system) ? "default" : system;
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#setCurrentSystem(String)
     */
    @Override
    public void setCurrentSystem(String system) {
        if (!StringUtils.isBlank(system)) {
            currentSystem.set(system);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#getCurrentModule()
     */
    @Override
    public String getCurrentModule() {
        String module = currentModule.get();
        return StringUtils.isBlank(module) ? "default" : module;
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#setCurrentModule(String)
     */
    @Override
    public void setCurrentModule(String module) {
        if (!StringUtils.isBlank(module)) {
            currentModule.set(module);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#removeCurrentUserCode()
     */
    @Override
    public void removeCurrentUserCode() {
        currentUser.remove();
    }

    /**
     * {@inheritDoc}
     *
     * @see SessionDataStore#clean()
     */
    @Override
    public void clean() {
        currentSystem.remove();
        currentModule.remove();
        currentUser.remove();
    }
}
