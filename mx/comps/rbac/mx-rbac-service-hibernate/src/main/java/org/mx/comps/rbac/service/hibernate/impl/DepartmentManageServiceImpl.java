package org.mx.comps.rbac.service.hibernate.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.comps.rbac.service.impl.DepartmentManageServiceCommonImpl;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA实现的部门管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("departmentManageServiceHibernate")
public class DepartmentManageServiceImpl extends DepartmentManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(DepartmentManageServiceCommonImpl.class);

    @Autowired
    @Qualifier("generalDictAccessor")
    private GeneralDictAccessor accessor = null;

    /**
     * {@inheritDoc}
     * @see DepartmentManageServiceCommonImpl#save(Department)
     */
    @Override
    protected Department save(Department department) {
        return accessor.save(department, false);
    }

    /**
     * {@inheritDoc}
     *
     * @see DepartmentManageService#saveDepartment(DepartInfo)
     */
    @Transactional
    @Override
    public Department saveDepartment(DepartInfo departInfo) {
        super.accessor = accessor;
        return super.saveDepartment(departInfo);
    }
}
