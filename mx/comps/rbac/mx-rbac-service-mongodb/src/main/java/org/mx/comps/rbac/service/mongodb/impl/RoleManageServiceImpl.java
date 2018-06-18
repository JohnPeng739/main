package org.mx.comps.rbac.service.mongodb.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.comps.rbac.service.impl.RoleManageServiceCommonImpl;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于Hibernate JPA实现的角色管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("roleManageServiceMongodb")
public class RoleManageServiceImpl extends RoleManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(RoleManageServiceCommonImpl.class);

    private GeneralDictAccessor accessor;

    /**
     * 默认的构造函数
     *
     * @param accessor 字典数据实体访问器
     */
    @Autowired
    public RoleManageServiceImpl(@Qualifier("generalDictAccessor") GeneralDictAccessor accessor) {
        super();
        this.accessor = accessor;
    }

    /**
     * {@inheritDoc}
     *
     * @see RoleManageServiceCommonImpl#save(Role)
     */
    @Override
    public Role save(Role role) {
        Set<Privilege> oldPrivileges = new HashSet<>();
        Set<Account> oldAccounts = new HashSet<>();
        if (!StringUtils.isBlank(role.getId())) {
            Role checked = accessor.getById(role.getId(), Role.class);
            oldPrivileges.addAll(checked.getPrivileges());
            oldAccounts.addAll(checked.getAccounts());
        }
        accessor.save(role);
        Set<Privilege> privileges = role.getPrivileges();
        Set<Account> accounts = role.getAccounts();
        for (Privilege privilege : privileges) {
            if (oldPrivileges.contains(privilege)) {
                oldPrivileges.remove(privilege);
            } else {
                privilege.getRoles().add(role);
                accessor.save(privilege);
            }
        }
        for (Privilege privilege : oldPrivileges) {
            privilege.getRoles().remove(role);
            accessor.save(privilege);
        }
        for (Account account : accounts) {
            if (oldAccounts.contains(account)) {
                oldAccounts.remove(account);
            } else {
                account.getRoles().add(role);
                accessor.save(account);
            }
        }
        for (Account account : oldAccounts) {
            account.getRoles().remove(role);
            accessor.save(account);
        }
        return role;
    }

    /**
     * {@inheritDoc}
     *
     * @see RoleManageService#saveRole(RoleInfo)
     */
    @Override
    public Role saveRole(RoleInfo roleInfo) {
        super.accessor = accessor;
        return super.saveRole(roleInfo);
    }
}
