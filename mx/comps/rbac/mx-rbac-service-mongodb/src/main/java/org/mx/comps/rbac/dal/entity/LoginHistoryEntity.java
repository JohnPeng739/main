package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 账户登录历史实体类定义
 *
 * @author : john.peng created on date : 2017/11/12
 */
@Document(collection = "loginHistory")
public class LoginHistoryEntity extends BaseEntity implements LoginHistory {
    @DBRef
    private Account account;
    @Indexed
    private long loginTime;
    @Indexed
    private long logoutTime;
    @Indexed
    private boolean online;

    @Override
    public int compareTo(LoginHistory o) {
        return (int) (loginTime - o.getLoginTime());
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#getAccount()
     */
    @Override
    public Account getAccount() {
        return account;
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#setAccount(Account)
     */
    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#getLoginTime()
     */
    @Override
    public long getLoginTime() {
        return loginTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#setLoginTime(long)
     */
    @Override
    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#getLogoutTime()
     */
    @Override
    public long getLogoutTime() {
        return logoutTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#setLogoutTime(long)
     */
    @Override
    public void setLogoutTime(long logoutTime) {
        this.logoutTime = logoutTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#isOnline()
     */
    @Override
    public boolean isOnline() {
        return online;
    }

    /**
     * {@inheritDoc}
     *
     * @see LoginHistory#setOnline(boolean)
     */
    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }
}
