package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDict;

import java.util.Set;

/**
 * 用户账户对象定义接口
 *
 * @author : john.peng created on date : 2017/11/4
 */
public interface Account extends BaseDict {
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
     * 获取关联用户
     *
     * @return 用户
     */
    User getOwner();

    /**
     * 设置关联用户
     *
     * @param user 用户
     */
    void setOwner(User user);

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    Set<Role> getRoles();

    /**
     * 设置角色列表
     *
     * @param roles 角色列表
     */
    void setRoles(Set<Role> roles);

    /**
     * 获取账户的快捷工具栏
     *
     * @return 快捷工具栏对应菜单的path集合
     */
    String getFavoriteTools();

    /**
     * 设置账户快捷工具栏
     *
     * @param favoriteTools 快捷工具栏对应菜单的path集合
     */
    void setFavoriteTools(String favoriteTools);
}
