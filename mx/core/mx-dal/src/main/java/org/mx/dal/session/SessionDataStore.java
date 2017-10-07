package org.mx.dal.session;

/**
 *
 * 会话数据保存仓库，用于存放线程安全的会话信息。
 *
 * Created by john on 2017/8/18.
 */
public interface SessionDataStore {
    String getCurrentUserCode();
    void setCurrentUserCode(String userCode);
    void removeCurrentUserCode();
}
