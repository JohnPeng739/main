package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.RoleManageService;
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
     * @see RoleManageService#saveRole(Role)
     */
    @Transactional
    @Override
    public Role saveRole(Role role) {
        if (role == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String id = role.getId();
        if (!StringUtils.isBlank(id)) {
            Role checked = super.getById(id, Role.class);
            if (checked == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND);
            }
            checked.setDesc(role.getDesc());
            checked.setName(role.getName());
            checked.setAccounts(role.getAccounts());
            checked.setPrivileges(role.getPrivileges());
            checked.setValid(role.isValid());
            role = super.save(checked, false);
        } else {
            role = super.save(role, false);
        }
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("保存角色[code=%s, name=%s]信息成功。",
                    role.getCode(), role.getName()));
        }
        return role;
    }
}
