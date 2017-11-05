package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDict;

import java.util.List;

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
    List<Role> getRoles();

    /**
     * 设置角色列表
     *
     * @param roles 角色列表
     */
    void setRoles(List<Role> roles);
}
