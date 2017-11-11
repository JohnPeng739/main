package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.rest.vo.BaseDictVO;

import java.util.ArrayList;
import java.util.List;

public class PrivilegeVO extends BaseDictVO {
    private static final Log logger = LogFactory.getLog(PrivilegeVO.class);

    private List<RoleVO> roles;

    public static void transform(Privilege privilege, PrivilegeVO privilegeVO) {
        if (privilege == null || privilegeVO == null) {
            return;
        }
        BaseDictVO.transform(privilege, privilegeVO);
        List<Role> roles = privilege.getRoles();
        if (roles != null && !roles.isEmpty()) {
            privilegeVO.roles = RoleVO.transformRoleVOs(roles);
        }
    }

    public static void transform(PrivilegeVO privilegeVO, Privilege privilege) {
        if (privilege == null || privilegeVO == null) {
            return;
        }
        BaseDictVO.transform(privilegeVO, privilege);
        List<RoleVO> roles = privilegeVO.getRoles();
        if (roles != null && !roles.isEmpty()) {
            privilege.setRoles(RoleVO.transformRoles(roles));
        }
    }

    public static List<Privilege> transformPrivileges(List<PrivilegeVO> privilegeVOs) {
        if (privilegeVOs == null || privilegeVOs.isEmpty()) {
            return null;
        }
        List<Privilege> privileges = new ArrayList<>(privilegeVOs.size());
        for (PrivilegeVO vo : privilegeVOs) {
            try {
                Privilege privilege = EntityFactory.createEntity(Privilege.class);
                PrivilegeVO.transform(vo, privilege);
                privileges.add(privilege);
            } catch (EntityInstantiationException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(ex);
                }
            }
        }
        return privileges;
    }

    public static List<PrivilegeVO> transformPrivilegeVOs(List<Privilege> privileges) {
        if (privileges == null || privileges.isEmpty()) {
            return null;
        }
        List<PrivilegeVO> privilegeVOs = new ArrayList<>(privileges.size());
        for (Privilege privilege : privileges) {
            PrivilegeVO vo = new PrivilegeVO();
            PrivilegeVO.transform(privilege, vo);
            privilegeVOs.add(vo);
        }
        return privilegeVOs;
    }

    public void setRoles(List<RoleVO> roles) {
        this.roles = roles;
    }

    public List<RoleVO> getRoles() {
        return roles;
    }
}
