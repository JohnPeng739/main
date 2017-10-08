package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseDict;

/**
 * Created by john on 2017/10/8.
 */
public interface User extends BaseDict {
    String getPassword();
    boolean isOnline();
    String getFavorite();
    String getRoles();
    void setPassword(String password);
    void setOnline(boolean online);
    void setFavorite(String favorite);
    void setRoles(String roles);
}
