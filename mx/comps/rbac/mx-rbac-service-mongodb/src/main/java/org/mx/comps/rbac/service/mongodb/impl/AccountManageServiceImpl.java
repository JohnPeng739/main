package org.mx.comps.rbac.service.mongodb.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.impl.AccountManageServiceCommonImpl;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于Hibernate JPA实现的账户管理相关服务
 *
 * @author : john.peng created on date : 2017/11/12
 */
@Component("accountManageServiceMongodb")
public class AccountManageServiceImpl extends AccountManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(AccountManageServiceCommonImpl.class);

    private GeneralDictAccessor accessor;

    /**
     * 默认的构造函数
     *
     * @param accessor 字典数据实体访问器
     */
    @Autowired
    public AccountManageServiceImpl(@Qualifier("generalDictAccessor") GeneralDictAccessor accessor) {
        super();
        this.accessor = accessor;
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageServiceCommonImpl#save(Account)
     */
    @Override
    protected Account save(Account account) {
        Set<Role> oldRoles = new HashSet<>();
        if (!StringUtils.isBlank(account.getId())) {
            oldRoles.addAll(accessor.getById(account.getId(), Account.class).getRoles());
        }
        account = accessor.save(account);
        Set<Role> roles = account.getRoles();
        for (Role role : roles) {
            if (!oldRoles.contains(role)) {
                role.getAccounts().add(account);
                accessor.save(role);
            }
            oldRoles.remove(role);
        }
        for (Role role : oldRoles) {
            role.getAccounts().remove(account);
            accessor.save(role);
        }
        return account;
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#saveAccount(AccountInfo)
     */
    @Override
    public Account saveAccount(AccountInfo accountInfo) {
        super.accessor = accessor;
        return super.saveAccount(accountInfo);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#changePassword(String, String, String)
     */
    @Override
    public Account changePassword(String accountId, String oldPassword, String newPassword) {
        super.accessor = accessor;
        return super.changePassword(accountId, oldPassword, newPassword);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#login(String, String, boolean)
     */
    @Override
    public LoginHistory login(String accountCode, String password, boolean forced) {
        super.accessor = accessor;
        return super.login(accountCode, password, forced);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#logout(String)
     */
    @Override
    public LoginHistory logout(String accountId) {
        super.accessor = accessor;
        return super.logout(accountId);
    }
}
