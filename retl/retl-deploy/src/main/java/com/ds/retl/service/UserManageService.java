package com.ds.retl.service;

import com.ds.retl.dal.entity.User;
import com.ds.retl.exception.UserInterfaceErrorException;

/**
 * 用户相关操作的服务接口定义，如：登入、登出、修改密码等
 *
 * @author : john.peng created on date : 2017/10/8
 */
public interface UserManageService {
    /**
     * 初始化用户
     *
     * @return 成功返回用户对象，否则抛出异常
     * @throws UserInterfaceErrorException 初始化过程中发生的异常
     */
    User initUser() throws UserInterfaceErrorException;

    /**
     * 保存用户信息
     *
     * @param user 用户实体对象
     * @return 成功返回用户对象，否则抛出异常
     * @throws UserInterfaceErrorException 保存过程中发生的异常
     */
    User saveUser(User user) throws UserInterfaceErrorException;

    /**
     * 修改用户米阿莫
     *
     * @param userCode    用户代码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 成功返回用户对象，否则抛出异常
     * @throws UserInterfaceErrorException 修改过程中发生的异常
     */
    User changePassword(String userCode, String oldPassword, String newPassword) throws UserInterfaceErrorException;

    /**
     * 登入系统
     *
     * @param userCode 用户代码
     * @param password 密码
     * @return 成功返回用户对象，否则抛出异常
     * @throws UserInterfaceErrorException 登入过程中发生的异常
     */
    User login(String userCode, String password) throws UserInterfaceErrorException;

    /**
     * 登出系统
     *
     * @param userCode 用户代码
     * @throws UserInterfaceErrorException 登出过程中发生的异常
     */
    void logout(String userCode) throws UserInterfaceErrorException;
}
