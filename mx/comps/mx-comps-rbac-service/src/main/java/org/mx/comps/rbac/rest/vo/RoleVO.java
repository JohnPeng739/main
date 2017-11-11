package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.rest.vo.BaseDictVO;

import java.util.ArrayList;
import java.util.List;

public class RoleVO extends BaseDictVO {
    private static final Log logger = LogFactory.getLog(RoleVO.class);

    private List<AccountVO> accounts;
    private List<PrivilegeVO> priviledges;

    public static void transform(Role role, RoleVO roleVO) {
        if (role == null || roleVO == null) {
            return;
        }
        BaseDictVO.transform(role, roleVO);
        List<Account> accounts = role.getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            roleVO.setAccounts(AccountVO.transformAccountVOs(accounts));
        }
        List<Privilege> privileges = role.getPrivileges();
        if (privileges != null && !privileges.isEmpty()) {
            roleVO.setPriviledges(PrivilegeVO.transformPrivilegeVOs(privileges));
        }
    }

    public static void transform(RoleVO roleVO, Role role) {
        if (role == null || roleVO == null) {
            return;
        }
        BaseDictVO.transform(roleVO, role);
        List<AccountVO> accountVOs = roleVO.getAccounts();
        if (accountVOs != null && !accountVOs.isEmpty()) {
            role.setAccounts(AccountVO.transformAccounts(accountVOs));
        }

        List<PrivilegeVO> privilegeVOs = roleVO.getPriviledges();
        if (privilegeVOs != null && !privilegeVOs.isEmpty()) {
            role.setPrivileges(PrivilegeVO.transformPrivileges(privilegeVOs));
        }
    }

    public static List<Role> transformRoles(List<RoleVO> rolesVOs) {
        if (rolesVOs == null || rolesVOs.isEmpty()) {
            return null;
        }
        List<Role> roles = new ArrayList<>(rolesVOs.size());
        for (RoleVO vo : rolesVOs) {
            try {
                Role role = EntityFactory.createEntity(Role.class);
                RoleVO.transform(vo, role);
                roles.add(role);
            } catch (EntityInstantiationException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(ex);
                }
            }
        }
        return roles;
    }

    public static List<RoleVO> transformRoleVOs(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        List<RoleVO> roleVOs = new ArrayList<>(roles.size());
        for (Role role : roles) {
            RoleVO vo = new RoleVO();
            RoleVO.transform(role, vo);
            roleVOs.add(vo);
        }
        return roleVOs;
    }

    public void setAccounts(List<AccountVO> accounts) {
        this.accounts = accounts;
    }

    public void setPriviledges(List<PrivilegeVO> priviledges) {
        this.priviledges = priviledges;
    }

    public List<AccountVO> getAccounts() {
        return accounts;
    }

    public List<PrivilegeVO> getPriviledges() {
        return priviledges;
    }
}
