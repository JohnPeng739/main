package org.mx.comps.rbac.service.hibernate.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.comps.rbac.service.impl.RoleManageServiceCommonImpl;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA实现的角色管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("roleManageServiceHibernate")
public class RoleManageServiceImpl extends RoleManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(RoleManageServiceCommonImpl.class);

    private GeneralDictAccessor accessor;

    /**
     * 默认的构造函数
     *
     * @param accessor 字典类型数据实体访问器
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
    protected Role save(Role role) {
        return accessor.save(role);
    }

    /**
     * {@inheritDoc}
     *
     * @see RoleManageService#saveRole(RoleInfo)
     */
    @Transactional
    @Override
    public Role saveRole(RoleInfo roleInfo) {
        super.accessor = accessor;
        return super.saveRole(roleInfo);
    }
}
