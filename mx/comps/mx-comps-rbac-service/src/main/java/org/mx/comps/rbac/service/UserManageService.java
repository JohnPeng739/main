package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceErrorException;

/**
 * 用户管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/10
 */
public interface UserManageService {
    User deleteUser(String id) throws UserInterfaceErrorException;
}
