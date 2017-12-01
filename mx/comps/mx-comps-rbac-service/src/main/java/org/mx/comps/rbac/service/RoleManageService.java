package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.service.GeneralDictAccessor;

/**
 * 角色管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/23
 */
public interface RoleManageService extends GeneralDictAccessor {
    /**
     * 保存角色相关信息
     *
     * @param role 角色实体对象
     * @return 保存后的角色实体对象
     */
    Role saveRole(Role role);

}
