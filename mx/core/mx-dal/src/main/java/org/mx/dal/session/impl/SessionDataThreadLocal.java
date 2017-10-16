package org.mx.dal.session.impl;

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
    private static ThreadLocal<String> currentUser = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "";
        }
    };

    /**
     * {@inheritDoc}
     * @see SessionDataStore#getCurrentUserCode()
     */
    @Override
    public String getCurrentUserCode() {
        return currentUser.get();
    }

    /**
     * {@inheritDoc}
     * @see SessionDataStore#setCurrentUserCode(String)
     */
    @Override
    public void setCurrentUserCode(String userCode) {
        currentUser.set(userCode);
    }

    /**
     * {@inheritDoc}
     * @see SessionDataStore#removeCurrentUserCode()
     */
    @Override
    public void removeCurrentUserCode() {
        currentUser.remove();
    }
}
