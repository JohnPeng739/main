package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 基于Hibernate JPA实现的部门管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component("departmentManageService")
public class DepartmentManageServiceImpl extends DepartmentManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(DepartmentManageServiceCommonImpl.class);

    @Autowired
    @Qualifier("generalDictEntityAccessorMongodb")
    private GeneralDictAccessor accessor = null;

    /**
     * {@inheritDoc}
     *
     * @see DepartmentManageService#saveDepartment(DepartInfo)
     */
    @Override
    public Department saveDepartment(DepartInfo departInfo) {
        super.accessor = accessor;
        return super.saveDepartment(departInfo);
    }
}
