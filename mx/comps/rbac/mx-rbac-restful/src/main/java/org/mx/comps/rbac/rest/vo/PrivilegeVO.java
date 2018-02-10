package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.EntityFactory;
import org.mx.service.rest.vo.BaseDictTreeVO;
import org.mx.service.rest.vo.BaseDictVO;

import java.util.*;

public class PrivilegeVO extends BaseDictVO {
    private static final Log logger = LogFactory.getLog(PrivilegeVO.class);

    private List<RoleVO> roles;

    public static PrivilegeVO transform(Privilege privilege, boolean iterate) {
        if (privilege == null) {
            return null;
        }
        PrivilegeVO privilegeVO = new PrivilegeVO();
        BaseDictVO.transform(privilege, privilegeVO);
        if (iterate) {
            privilegeVO.roles = RoleVO.transform(privilege.getRoles());
        }
        return privilegeVO;
    }

    public static List<PrivilegeVO> transform(Collection<Privilege> privileges) {
        List<PrivilegeVO> privilegeVOS = new ArrayList<>();
        if (privileges == null || privileges.isEmpty()) {
            return privilegeVOS;
        }
        privileges.forEach(privilege -> {
            PrivilegeVO privilegeVO = PrivilegeVO.transform(privilege, false);
            if (privilegeVO != null) {
                privilegeVOS.add(privilegeVO);
            }
        });
        return privilegeVOS;
    }

    public List<RoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleVO> roles) {
        this.roles = roles;
    }
}
