package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Hibernate JPA实现的部门管理服务实现类。
 *
 * @author : john.peng created on date : 2017/11/19
 */
@Component
public class DepartmentManageServiceImpl extends GeneralDictEntityAccessorImpl implements DepartmentManageService {
    private static final Log logger = LogFactory.getLog(DepartmentManageServiceImpl.class);

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see DepartmentManageService#save(String, Department)
     */
    @Override
    public Department save(String id, Department department) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(id) || department == null) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            Department checked = super.getById(id, Department.class);
            if (checked == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.DEPARTMENT_NOT_FOUND);
            }
            checked.setDesc(department.getDesc());
            checked.setName(department.getName());
            checked.setManager(department.getManager());
            checked.setEmployees(department.getEmployees());
            department = super.save(checked, false);
            if (operateLogService != null) {
                operateLogService.writeLog(String.format("保存部门[code=%s, name=%s]信息成功。",
                        department.getCode(), department.getName()));
            }
            return department;
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }
}
