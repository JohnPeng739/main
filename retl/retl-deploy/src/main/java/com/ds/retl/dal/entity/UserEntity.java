package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 基于Hibernate JPA实现的用户信息实体类
 *
 * @author : john.peng created on date : 2017/10/8
 */
@Entity
@Table(name = "TB_USER")
public class UserEntity extends BaseDictEntity implements User {
    @Column(name = "PASSWORD", nullable = false, length = 64)
    private String password;
    @Column(name = "ONLINE_STATUS")
    private boolean online = false;
    @Column(name = "FAVORITE")
    private String favorite;
    @Column(name = "ROLES")
    private String roles;

    /**
     * {@inheritDoc}
     *
     * @see User#getPassword()
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setPassword(String)
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#isOnline()
     */
    @Override
    public boolean isOnline() {
        return online;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setOnline(boolean)
     */
    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getFavorite()
     */
    @Override
    public String getFavorite() {
        return favorite;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setFavorite(String)
     */
    @Override
    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getRoles()
     */
    @Override
    public String getRoles() {
        return roles;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setRoles(String)
     */
    @Override
    public void setRoles(String roles) {
        this.roles = roles;
    }
}
