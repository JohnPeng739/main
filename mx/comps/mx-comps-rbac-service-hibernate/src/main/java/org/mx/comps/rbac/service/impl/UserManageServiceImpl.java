package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralEntityAccessorImpl;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA开发的用户管理接口实现
 *
 * @author : john.peng created on date : 2017/11/10
 */
@Component("userManageService")
public class UserManageServiceImpl extends GeneralEntityAccessorImpl implements UserManageService {
    private static final Log logger = LogFactory.getLog(UserManageServiceImpl.class);

    @Autowired
    private AccountManageService accountManageService = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#allocateAccount(AccountInfo)
     */
    @Override
    public Account allocateAccount(AccountInfo accountInfo) {
        if (accountInfo == null || StringUtils.isBlank(accountInfo.getUserId()) ||
                StringUtils.isBlank(accountInfo.getCode())) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        User user = super.getById(accountInfo.getUserId(), User.class);
        if (user == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
        }
        Account account = accountManageService.getByCode(accountInfo.getCode(), Account.class);
        if (account != null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_HAS_EXIST);
        }
        account = EntityFactory.createEntity(Account.class);
        account.setCode(accountInfo.getCode());
        account.setPassword(accountInfo.getPassword());
        account.setName(user.getFullName());
        account.setDesc(user.getDesc());
        account.setOwner(user);
        for (String roleId : accountInfo.getRoleIds()) {
            Role role = super.getById(roleId, Role.class);
            if (role == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND);
            }
            account.getRoles().add(role);
        }
        account = accountManageService.saveAccount(account);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("为用户[%s]分配账户[%s]成功。", user.getFullName(), account.getCode()));
        }
        return account;
    }

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#saveUser(User)
     */
    @Transactional
    @Override
    public User saveUser(User user) {
        if (user == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String userId = user.getId();
        if (!StringUtils.isBlank(userId)) {
            User checked = super.getById(userId, User.class);
            if (checked == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The User entity[%s] not found.", userId));
                }
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
            }
            checked.setBirthday(user.getBirthday());
            checked.setDepartment(user.getDepartment());
            checked.setDesc(user.getDesc());
            checked.setFirstName(user.getFirstName());
            checked.setMiddleName(user.getMiddleName());
            checked.setLastName(user.getLastName());
            checked.setSex(user.getSex());
            checked.setStation(user.getStation());
            checked.setValid(user.isValid());
            user = super.save(checked, false);
        } else {
            user = super.save(user, false);
        }
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("保存用户[name=%s]信息成功。", user.getFullName()));
        }
        return user;
    }
}
