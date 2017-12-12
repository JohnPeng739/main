package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDict;

import java.util.List;
import java.util.Set;

/**
 * 角色对象定义接口
 *
 * @author : john.peng created on date : 2017/11/4
 */
public interface Role extends BaseDict {
    /**
     * 获取账户列表
     *
     * @return 账户列表
     */
    Set<Account> getAccounts();

    /**
     * 设置账户列表
     *
     * @param accounts 账户列表
     */
    void setAccounts(Set<Account> accounts);

    /**
     * 获取特权列表
     *
     * @return 特权列表
     */
    Set<Privilege> getPrivileges();

    /**
     * 设置特权列表
     *
     * @param privileges 特权列表
     */
    void setPrivileges(Set<Privilege> privileges);
}
