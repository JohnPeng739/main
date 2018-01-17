package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 账户登录历史记录
 *
 * @author : john.peng created on date : 2017/11/12
 */
public interface LoginHistory extends Base, Comparable<LoginHistory> {
    /**
     * 获取账户
     *
     * @return 账户
     */
    Account getAccount();

    /**
     * 设置账户
     *
     * @param account 账户
     */
    void setAccount(Account account);

    /**
     * 获取访问令牌
     *
     * @return 令牌
     */
    String getToken();

    /**
     * 设置访问令牌
     *
     * @param token 令牌
     */
    void setToken(String token);

    /**
     * 获取登录时间
     *
     * @return 时间
     */
    long getLoginTime();

    /**
     * 设置登录时间
     *
     * @param loginTime 时间
     */
    void setLoginTime(long loginTime);

    /**
     * 获取登出时间
     *
     * @return 时间
     */
    long getLogoutTime();

    /**
     * 设置登出时间
     *
     * @param logoutTime 时间
     */
    void setLogoutTime(long logoutTime);

    /**
     * 获取是否在线
     *
     * @return 是否在线
     */
    boolean isOnline();

    /**
     * 设置是否在线
     *
     * @param online 是否在线
     */
    void setOnline(boolean online);
}
