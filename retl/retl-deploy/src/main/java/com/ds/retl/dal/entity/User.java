package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseDict;

/**
 * 基于Hibernate JPA实现的用户信息实体类
 *
 * @author : john.peng created on date : 2017/10/8
 * @see BaseDict
 */
public interface User extends BaseDict {
    /**
     * 获取密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 设置密码
     *
     * @param password 密码
     */
    void setPassword(String password);

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

    /**
     * 获取常用工具
     *
     * @return 常用工具
     */
    String getFavorite();

    /**
     * 设置常用工具
     *
     * @param favorite 常用工具
     */
    void setFavorite(String favorite);

    /**
     * 获取角色组
     *
     * @return 角色组
     */
    String getRoles();

    /**
     * 设置角色组
     *
     * @param roles 角色组
     */
    void setRoles(String roles);
}
