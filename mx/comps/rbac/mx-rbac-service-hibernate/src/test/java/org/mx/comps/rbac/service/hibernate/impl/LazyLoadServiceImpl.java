package org.mx.comps.rbac.service.hibernate.impl;

import org.mx.comps.rbac.dal.entity.*;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述： 测试懒加载数据的服务
 *
 * @author john peng
 * Date time 2018/8/10 下午10:22
 */
@Component
public class LazyLoadServiceImpl {
    private GeneralAccessor accessor;

    @Autowired
    public LazyLoadServiceImpl(@Qualifier("generalAccessor") GeneralAccessor accessor) {
        super();
        this.accessor = accessor;
    }

    @Transactional(readOnly = true)
    public Account getAccoutById(String id) {
        Account account = accessor.getById(id, Account.class);
        account.getRoles().size();
        return account;
    }

    @Transactional(readOnly = true)
    public Accredit getAccreditById(String id) {
        Accredit accredit = accessor.getById(id, Accredit.class);
        accredit.getRoles().size();
        return accredit;
    }

    @Transactional(readOnly = true)
    public Role getRoleById(String id) {
        Role role = accessor.getById(id, Role.class);
        role.getPrivileges().size();
        role.getAccounts().size();
        return role;
    }

    @Transactional(readOnly = true)
    public Privilege getPrivilegeById(String id) {
        Privilege privilege = accessor.getById(id, Privilege.class);
        privilege.getRoles().size();
        return privilege;
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(String id) {
        Department department = accessor.getById(id, Department.class);
        department.getEmployees().size();
        department.getChildren().size();
        return department;
    }
}
