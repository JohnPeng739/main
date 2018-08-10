package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;

/**
 * 的角色管理服务公共实现。
 *
 * @author : john.peng created on date : 2017/11/19
 */
public abstract class RoleManageServiceCommonImpl implements RoleManageService {
    private static final Log logger = LogFactory.getLog(RoleManageServiceCommonImpl.class);

    protected GeneralDictAccessor accessor = null;

    /**
     * 保存角色实体对象
     *
     * @param role 角色实体对象
     * @return 保存后的角色实体对象
     */
    protected abstract Role save(Role role);

    /**
     * {@inheritDoc}
     *
     * @see RoleManageService#saveRole(RoleInfo)
     */
    @Override
    public Role saveRole(RoleInfo roleInfo) {
        if (roleInfo == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String id = roleInfo.getRoleId();
        Role role;
        if (!StringUtils.isBlank(id)) {
            role = accessor.getById(id, Role.class);
            if (role == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND);
            }
        } else {
            role = EntityFactory.createEntity(Role.class);
        }
        role.setCode(roleInfo.getCode());
        role.setName(roleInfo.getName());
        role.setDesc(roleInfo.getDesc());
        if (role.getAccounts() != null && !role.getAccounts().isEmpty()) {
            role.getAccounts().clear();
        }
        if (roleInfo.getAccountIds() != null && !roleInfo.getAccountIds().isEmpty()) {
            for (String accountId : roleInfo.getAccountIds()) {
                Account account = accessor.getById(accountId, Account.class);
                if (account == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
                }
                role.getAccounts().add(account);
            }
        }
        if (role.getPrivileges() != null) {
            role.getPrivileges().clear();
            for (String privilegeId : roleInfo.getPrivilegeIds()) {
                Privilege privilege = accessor.getById(privilegeId, Privilege.class);
                if (privilege == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.PRIVILEGE_NOT_FOUND);
                }
                role.getPrivileges().add(privilege);
            }
        }
        role.setValid(roleInfo.isValid());
        return this.save(role);
    }
}
