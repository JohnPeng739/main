package org.mx.dal.session.impl;

import org.mx.dal.session.SessionDataStore;
import org.springframework.stereotype.Component;

/**
 * Created by john on 2017/8/18.
 */
@Component("sessionDataThreadLocal")
public class SessionDataThreadLocal implements SessionDataStore {

    private static ThreadLocal<String> currentUser = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "";
        }
    };

    @Override
    public String getCurrentUserCode() {
        return currentUser.get();
    }

    @Override
    public void setCurrentUserCode(String userCode) {
        currentUser.set(userCode);
    }

    @Override
    public void removeCurrentUserCode() {
        currentUser.remove();
    }
}
