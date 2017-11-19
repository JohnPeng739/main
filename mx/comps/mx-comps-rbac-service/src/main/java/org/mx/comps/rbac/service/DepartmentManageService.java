package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
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
     * @param id         待修改的部门实体ID
     * @param department 部门实体
     * @return 修改后的部门实体
     * @throws UserInterfaceErrorException 修改过程中发生的错误
     */
    Department save(String id, Department department) throws UserInterfaceErrorException;
}
