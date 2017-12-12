package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 基于Hibernate JPA实现的角色管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("roleManageService")
public class RoleManageServiceImpl extends RoleManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(RoleManageServiceCommonImpl.class);

    @Autowired
    @Qualifier("generalDictEntityAccessorMongodb")
    private GeneralDictAccessor accessor = null;

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
