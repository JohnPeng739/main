package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictTree;

import java.util.Set;

/**
 * 部门对象定义接口
 *
 * @author : john.peng created on date : 2017/11/4
 */
public interface Department extends BaseDictTree {
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
     * 添加一个员工
     *
     * @param employee 代表员工的用户对象
     */
    void addEmployee(User employee);

    /**
     * 删除一个员工
     *
     * @param employee 代表员工的用户对象
     */
    void removeEmployee(User employee);

    /**
     * 获取雇员列表
     *
     * @return 雇员列表
     */
    Set<User> getEmployees();

    /**
     * 设置雇员列表
     *
     * @param employees 雇员列表
     */
    void setEmployees(Set<User> employees);
}
