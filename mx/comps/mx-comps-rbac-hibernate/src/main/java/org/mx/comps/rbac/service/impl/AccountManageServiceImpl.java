package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 基于Hibernate JPA实现的账户管理相关服务
 *
 * @author : john.peng created on date : 2017/11/12
 */
@Component
public class AccountManageServiceImpl extends GeneralDictEntityAccessorImpl implements AccountManageService {
    private static final Log logger = LogFactory.getLog(AccountManageServiceImpl.class);

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#changePassword(String, String, String)
     */
    @Override
    public Account changePassword(String accountId, String oldPassword, String newPassword)
            throws UserInterfaceErrorException {
        try {
            Account account = super.getById(accountId, Account.class);
            if (account == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.ACCOUNT_NOT_FOUND);
            }
            try {
                if (account.getPassword().equals(DigestUtils.md5(oldPassword))) {
                    // the old password is matched.
                    account.setPassword(DigestUtils.md5(newPassword));
                    return super.save(account);
                } else {
                    throw new UserInterfaceErrorException(UserInterfaceErrors.ACCOUNT_PASSWORD_NOT_MATCHED);
                }
            } catch (NoSuchAlgorithmException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(ex);
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.ACCOUNT_DIGEST_PASSWORD_FAIL);
            }
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#login(String, String, boolean)
     */
    @Override
    public LoginHistory login(String accountCode, String password, boolean forced) throws UserInterfaceErrorException {
        try {
            Account account = super.getByCode(accountCode, Account.class);
            if (account == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.ACCOUNT_NOT_FOUND);
            }
            List<ConditionTuple> tuples = Arrays.asList(new ConditionTuple("account", account),
                    new ConditionTuple("online", true));
            List<LoginHistory> loginHistories = super.find(tuples, LoginHistory.class);
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
                    throw new UserInterfaceErrorException(UserInterfaceErrors.ACCOUNT_ALREADY_LOGINED);
                }
            } else {
                // 新登录
                loginHistory = EntityFactory.createEntity(LoginHistory.class);
                loginHistory.setAccount(account);
            }
            loginHistory.setLoginTime(new Date().getTime());
            loginHistory.setOnline(true);
            return super.save(loginHistory);
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        } catch (EntityInstantiationException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#logout(String)
     */
    @Override
    public LoginHistory logout(String accountId) throws UserInterfaceErrorException {
        try {
            Account account = super.getById(accountId, Account.class);
            if (account == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.ACCOUNT_NOT_FOUND);
            }
            List<ConditionTuple> tuples = Arrays.asList(new ConditionTuple("account", account),
                    new ConditionTuple("online", true));
            List<LoginHistory> loginHistories = super.find(tuples, LoginHistory.class);
            if (loginHistories == null || loginHistories.isEmpty()) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.ACCOUNT_NOT_LOGIN);
            } else {
                if (loginHistories.size() > 1) {
                    // 根据登录时间排序
                    Collections.sort(loginHistories);
                }
                LoginHistory loginHistory = loginHistories.get(0);
                loginHistory.setLogoutTime(new Date().getTime());
                loginHistory.setOnline(false);
                return super.save(loginHistory);
            }
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }
}
