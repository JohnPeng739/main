package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 账户登录历史实体类定义
 *
 * @author : john.peng created on date : 2017/11/12
 */
@Entity
@Table(name = "TB_LOGIN_HISTORY")
public class LoginHistoryEntity extends BaseEntity implements LoginHistory {
    @ManyToOne(targetEntity = AccountEntity.class)
    private Account account;
    @Column(name = "LOGIN_TIME")
    private long loginTime;
    @Column(name = "LOGOUT_TIME")
    private long logoutTime;
    @Column(name = "ONLINE_STATUS")
    private boolean online;

    @Override
    public int compareTo(LoginHistory o) {
        return (int)(loginTime - o.getLoginTime());
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#getAccount()
     */
    @Override
    public Account getAccount() {
        return account;
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#getLoginTime()
     */
    @Override
    public long getLoginTime() {
        return loginTime;
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#getLogoutTime()
     */
    @Override
    public long getLogoutTime() {
        return logoutTime;
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#isOnline()
     */
    @Override
    public boolean isOnline() {
        return online;
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#setAccount(Account)
     */
    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#setLoginTime(long)
     */
    @Override
    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#setLogoutTime(long)
     */
    @Override
    public void setLogoutTime(long logoutTime) {
        this.logoutTime = logoutTime;
    }

    /**
     * {@inheritDoc}
     * @see LoginHistory#setOnline(boolean)
     */
    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }
}
