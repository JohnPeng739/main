package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Department;
import org.mx.dal.service.GeneralDictAccessor;

/**
 * 部门管理的服务接口定义
 *
 * @author : john.peng created on date : 2017/11/19
 */
public interface DepartmentManageService extends GeneralDictAccessor {
    /**
     * 修改指定ID的部门信息
     *
     * @param department 部门实体
     * @return 修改后的部门实体
     */
    Department saveDepartment(Department department);
}
