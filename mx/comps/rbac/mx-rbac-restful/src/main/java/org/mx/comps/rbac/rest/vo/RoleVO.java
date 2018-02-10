package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.EntityFactory;
import org.mx.service.rest.vo.BaseDictVO;

import java.util.*;

public class RoleVO extends BaseDictVO {
    private static final Log logger = LogFactory.getLog(RoleVO.class);

    private List<AccountVO> accounts;
    private List<PrivilegeVO> priviledges;

    public static RoleVO transform(Role role, boolean iterate) {
        if (role == null) {
            return null;
        }
        RoleVO roleVO = new RoleVO();
        BaseDictVO.transform(role, roleVO);
        if (iterate) {
            roleVO.accounts = AccountVO.transform(role.getAccounts());
            roleVO.priviledges = PrivilegeVO.transform(role.getPrivileges());
        }
        return roleVO;
    }

    public static List<RoleVO> transform(Collection<Role> roles) {
        List<RoleVO> roleVOs = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            return roleVOs;
        }
        roles.forEach(role -> {
            RoleVO roleVO = RoleVO.transform(role, false);
            if (roleVO != null) {
                roleVOs.add(roleVO);
            }
        });
        return roleVOs;
    }

    public List<AccountVO> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountVO> accounts) {
        this.accounts = accounts;
    }

    public List<PrivilegeVO> getPriviledges() {
        return priviledges;
    }

    public void setPriviledges(List<PrivilegeVO> priviledges) {
        this.priviledges = priviledges;
    }
}
