package org.mx.dal.session;

/**
 * 会话数据访问接口定义，用于存放线程安全的会话信息。
 *
 * @author : john.peng date : 2017/8/18
 */
public interface SessionDataStore {
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
}
