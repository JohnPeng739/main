package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 用户管理接口公共实现
 *
 * @author : john.peng created on date : 2017/11/10
 */
public class UserManageServiceCommonImpl implements UserManageService {
    private static final Log logger = LogFactory.getLog(UserManageServiceCommonImpl.class);

    protected GeneralDictAccessor accessor = null;

    @Autowired
    private AccountManageService accountManageService = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#allocateAccount(AccountManageService.AccountInfo)
     */
    @Override
    public Account allocateAccount(AccountManageService.AccountInfo accountInfo) {
        if (accountInfo == null || StringUtils.isBlank(accountInfo.getOwnerId()) ||
                StringUtils.isBlank(accountInfo.getCode())) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        User user = accessor.getById(accountInfo.getOwnerId(), User.class);
        if (user == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
        }
        Account account = accessor.getByCode(accountInfo.getCode(), Account.class);
        if (account != null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_HAS_EXIST);
        }
        account = accountManageService.saveAccount(accountInfo);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("为用户[%s]分配账户[%s]成功。", user.getFullName(), account.getCode()));
        }
        return account;
    }

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#saveUser(UserInfo)
     */
    @Override
    public User saveUser(UserInfo userInfo) {
        if (userInfo == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String userId = userInfo.getUserId();
        User user;
        if (!StringUtils.isBlank(userId)) {
            user = accessor.getById(userId, User.class);
            if (user == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The User entity[%s] not found.", userId));
                }
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
            }
        } else {
            user = EntityFactory.createEntity(User.class);
        }

        if (!StringUtils.isBlank(userInfo.getDepartId())) {
            Department depart = accessor.getById(userInfo.getDepartId(), Department.class);
            if (depart == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.DEPARTMENT_NOT_FOUND);
            }
            user.setDepartment(depart);
        }
        long birthday = userInfo.getBirthday();
        if (birthday > 0) {
            user.setBirthday(new Date(birthday));
        }
        user.setDesc(userInfo.getDesc());
        user.setFirstName(userInfo.getFirstName());
        user.setMiddleName(userInfo.getMiddleName());
        user.setLastName(userInfo.getLastName());
        user.setSex(userInfo.getSex());
        user.setStation(userInfo.getStation());
        user.setValid(userInfo.isValid());
        user = accessor.save(user, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("保存用户[name=%s]信息成功。", user.getFullName()));
        }
        return user;
    }
}
