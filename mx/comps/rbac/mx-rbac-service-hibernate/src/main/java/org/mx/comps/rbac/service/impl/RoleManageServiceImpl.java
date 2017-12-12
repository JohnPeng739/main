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
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA实现的角色管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("roleManageService")
public class RoleManageServiceImpl extends GeneralDictEntityAccessorImpl implements RoleManageService {
    private static final Log logger = LogFactory.getLog(RoleManageServiceImpl.class);

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see RoleManageService#saveRole(RoleInfo)
     */
    @Transactional
    @Override
    public Role saveRole(RoleInfo roleInfo) {
        if (roleInfo == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String id = roleInfo.getRoleId();
        Role role;
        if (!StringUtils.isBlank(id)) {
            role = super.getById(id, Role.class);
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
        for (String accountId : roleInfo.getAccountIds()) {
            Account account = super.getById(accountId, Account.class);
            if (account == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
            }
            role.getAccounts().add(account);
        }
        if (role.getPrivileges() != null && !role.getPrivileges().isEmpty()) {
            role.getPrivileges().clear();
        }
        for (String privilegeId : roleInfo.getPrivilegeIds()) {
            Privilege privilege = super.getById(privilegeId, Privilege.class);
            if (privilege == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.PRIVILEGE_NOT_FOUND);
            }
            role.getPrivileges().add(privilege);
        }
        role.setValid(roleInfo.isValid());
        role = super.save(role, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("保存角色[code=%s, name=%s]信息成功。",
                    roleInfo.getCode(), roleInfo.getName()));
        }
        return role;
    }
}
