package com.ds.retl.dal.entity;

import com.ds.retl.dal.entity.User;
import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by john on 2017/10/8.
 */
@Entity
@Table(name = "TB_USER")
public class UserEntity extends BaseDictEntity implements User {
    @Column(name = "PASSWORD", nullable = false, length = 64)
    private String password;
    @Column(name = "ONLINE")
    private boolean online = false;
    @Column(name = "FAVORITE")
    private String favorite;
    @Column(name = "ROLES")
    private String roles;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public String getFavorite() {
        return favorite;
    }

    @Override
    public String getRoles() {
        return roles;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    @Override
    public void setRoles(String roles) {
        this.roles = roles;
    }
}
