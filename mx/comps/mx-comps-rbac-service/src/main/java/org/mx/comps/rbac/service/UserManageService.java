package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.dal.service.GeneralDictAccessor;

/**
 * 用户管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/10
 */
public interface UserManageService extends GeneralDictAccessor {
    /**
     * 修改用户信息
     *
     * @param userId 待修改用户ID
     * @param user   用户实体
     * @return 修改后的用户实体
     * @throws UserInterfaceErrorException 修改过程中发生的异常
     */
    User save(String userId, User user) throws UserInterfaceErrorException;
}
