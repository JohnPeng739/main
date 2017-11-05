package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDict;

import java.util.List;

/**
 * 特权对象定义接口
 *
 * @author : john.peng created on date : 2017/11/4
 */
public interface Privilege extends BaseDict {
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
