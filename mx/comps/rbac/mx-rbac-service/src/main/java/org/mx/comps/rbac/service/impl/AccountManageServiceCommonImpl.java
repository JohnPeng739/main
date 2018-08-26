package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.jwt.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 账户管理相关服务公共实现类
 *
 * @author : john.peng created on date : 2017/11/12
 */
public abstract class AccountManageServiceCommonImpl implements AccountManageService {
    private static final Log logger = LogFactory.getLog(AccountManageServiceCommonImpl.class);

    protected GeneralDictAccessor accessor = null;

    @Autowired
    private JwtService jwtService = null;

    /**
     * 保存账户实体对象
     *
     * @param account 账户实体对象
     * @return 保存后的账户实体对象
     */
    protected abstract Account save(Account account);

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#saveAccount(AccountInfo)
     */
    @Override
    public Account saveAccount(AccountInfo accountInfo) {
        if (accountInfo == null) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            String accountId = accountInfo.getAccountId();
            Account account;
            if (!StringUtils.isBlank(accountId)) {
                account = accessor.getById(accountId, Account.class);
                if (account == null) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("The Account entity[%s] not found.", accountId));
                    }
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
                }
                // 这里不允许修改密码，密码必须通过另外途径进行修改
            } else {
                String password = accountInfo.getPassword();
                if (StringUtils.isBlank(password)) {
                    password = "ds110119";
                }
                account = EntityFactory.createEntity(Account.class);
                account.setPassword(DigestUtils.md5(password));
            }
            account.setCode(accountInfo.getCode());
            if (StringUtils.isBlank(accountInfo.getOwnerId())) {
                if (!"admin".equals(accountInfo.getCode())) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOALLOCATE_USER);
                }
            } else {
                User owner = accessor.getById(accountInfo.getOwnerId(), User.class);
                if (owner == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND);
                }
                account.setOwner(owner);
                account.setName(owner.getFullName());
            }
            account.setDesc(accountInfo.getDesc());
            if (account.getRoles() != null && !account.getRoles().isEmpty()) {
                account.getRoles().clear();
            }
            for (String roleId : accountInfo.getRoleIds()) {
                Role role = accessor.getById(roleId, Role.class);
                if (role == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND);
                }
                account.getRoles().add(role);
            }
            account.setValid(accountInfo.isValid());
            return this.save(account);
        } catch (UserInterfaceDalErrorException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#changePassword(String, String, String)
     */
    @Override
    public Account changePassword(String accountId, String oldPassword, String newPassword) {
        Account account = accessor.getById(accountId, Account.class);
        if (account == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        if (account.getPassword().equals(DigestUtils.md5(oldPassword))) {
            // the old password is matched.
            account.setPassword(DigestUtils.md5(newPassword));
            return this.save(account);
        } else {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_PASSWORD_NOT_MATCHED);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#changePersonal(AccountPersonalInfo)
     */
    @Override
    public Account changePersonal(AccountPersonalInfo accountPersonalInfo) {
        Account account = accessor.getById(accountPersonalInfo.getId(), Account.class);
        if (account == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        account.setFavoriteTools(accountPersonalInfo.getFavoriteTools());
        return this.save(account);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#login(String, String, boolean)
     */
    @Override
    public LoginHistory login(String accountCode, String password, boolean forced) {
        Account account = accessor.getByCode(accountCode, Account.class);
        if (account == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        if (!DigestUtils.md5(password).equals(account.getPassword())) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_PASSWORD_NOT_MATCHED);
        }
        GeneralAccessor.ConditionGroup group = GeneralAccessor.ConditionGroup.and(
                GeneralAccessor.ConditionTuple.eq("account", account),
                GeneralAccessor.ConditionTuple.eq("online", true)
        );
        List<LoginHistory> loginHistories = accessor.find(group, LoginHistory.class);
        LoginHistory loginHistory;
        if (loginHistories != null && !loginHistories.isEmpty()) {
            // 已经登录
            if (forced) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The account[%s] has login, now login again.", accountCode));
                }
                // 强制重新登录
                if (loginHistories.size() > 1) {
                    // 根据登录时间排序
                    Collections.sort(loginHistories);
                }
                loginHistory = loginHistories.get(0);
            } else {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_ALREADY_LOGINED);
            }
        } else {
            // 新登录
            loginHistory = EntityFactory.createEntity(LoginHistory.class);
            loginHistory.setAccount(account);
        }
        loginHistory.setLoginTime(new Date().getTime());
        loginHistory.setOnline(true);
        // 设置令牌
        loginHistory.setToken(jwtService.signToken(account.getCode()));
        return accessor.save(loginHistory);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#logout(String)
     */
    @Override
    public LoginHistory logout(String accountId) {
        Account account = accessor.getById(accountId, Account.class);
        if (account == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        GeneralAccessor.ConditionGroup group = GeneralAccessor.ConditionGroup.and(
                GeneralAccessor.ConditionTuple.eq("account", account),
                GeneralAccessor.ConditionTuple.eq("online", true)
        );
        List<LoginHistory> loginHistories = accessor.find(group, LoginHistory.class);
        if (loginHistories == null || loginHistories.isEmpty()) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_LOGIN);
        } else {
            if (loginHistories.size() > 1) {
                // 根据登录时间排序
                Collections.sort(loginHistories);
            }
            LoginHistory loginHistory = loginHistories.get(0);
            loginHistory.setLogoutTime(new Date().getTime());
            loginHistory.setOnline(false);
            return accessor.save(loginHistory);
        }
    }
}
