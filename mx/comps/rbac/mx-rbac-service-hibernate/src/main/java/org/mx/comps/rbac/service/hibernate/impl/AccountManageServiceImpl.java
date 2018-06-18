package org.mx.comps.rbac.service.hibernate.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.impl.AccountManageServiceCommonImpl;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA实现的账户管理相关服务
 *
 * @author : john.peng created on date : 2017/11/12
 */
@Component("accountManageServiceHibernate")
public class AccountManageServiceImpl extends AccountManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(AccountManageServiceCommonImpl.class);

    private GeneralDictAccessor accessor;

    /**
     * 默认的构造函数
     *
     * @param accessor 字典类型数据实体访问器
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
        return accessor.save(account);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccountManageService#saveAccount(AccountInfo)
     */
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    @Override
    public LoginHistory logout(String accountId) {
        super.accessor = accessor;
        return super.logout(accountId);
    }
}
