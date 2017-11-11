package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDict;

import java.util.List;

/**
 * 部门对象定义接口
 *
 * @author : john.peng created on date : 2017/11/4
 */
public interface Department extends BaseDict {
    /**
     * 获取管理者
     *
     * @return 管理者
     */
    User getManager();

    /**
     * 设置管理者
     *
     * @param manager 管理者
     */
    void setManager(User manager);

    /**
     * 获取雇员列表
     *
     * @return 雇员列表
     */
    List<User> getEmployees();

    /**
     * 设置雇员列表
     *
     * @param employees 雇员列表
     */
    void setEmployees(List<User> employees);
}
