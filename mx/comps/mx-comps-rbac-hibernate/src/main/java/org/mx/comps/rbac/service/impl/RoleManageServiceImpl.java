package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Hibernate JPA实现的角色管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component
public class RoleManageServiceImpl extends GeneralDictEntityAccessorImpl implements RoleManageService {
    private static final Log logger = LogFactory.getLog(RoleManageServiceImpl.class);

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see RoleManageService#save(String, Role)
     */
    @Override
    public Role save(String id, Role role) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(id)) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            Role checked = super.getById(id, Role.class);
            if (checked == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.ROLE_NOT_FOUND);
            }
            checked.setDesc(role.getDesc());
            checked.setName(role.getName());
            checked.setPrivileges(role.getPrivileges());
            role = super.save(checked);
            if (operateLogService != null) {
                operateLogService.writeLog(String.format("保存角色[code=%s, name=%s]信息成功。",
                        role.getCode(), role.getName()));
            }
            return role;
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }
}
