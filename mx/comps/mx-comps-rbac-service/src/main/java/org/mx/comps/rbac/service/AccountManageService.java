package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.dal.service.GeneralDictAccessor;

/**
 * 账户管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/13
 */
public interface AccountManageService extends GeneralDictAccessor {
    /**
     * 修改账户信息
     *
     * @param id      待修改账户实体ID
     * @param account 账户实体
     * @return 修改成功后的账户实体
     * @throws UserInterfaceErrorException 修改过程中发生的异常
     */
    Account save(String id, Account account) throws UserInterfaceErrorException;

    /**
     * 修改账户密码
     *
     * @param accountId   账户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 账户对象
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     */
    Account changePassword(String accountId, String oldPassword, String newPassword) throws UserInterfaceErrorException;

    /**
     * 登录系统
     *
     * @param accountCode 账户代码
     * @param password    账户密码
     * @param forced      如果设置为true，则如果发生该账户已经登录的话仍然强制登录（踢掉上次登录），否则返回异常。
     * @return 登录历史对象
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     */
    LoginHistory login(String accountCode, String password, boolean forced) throws UserInterfaceErrorException;

    /**
     * 登出系统
     *
     * @param accountId 账户ID
     * @return 登录历史对象
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     */
    LoginHistory logout(String accountId) throws UserInterfaceErrorException;
}
