package org.mx.comps.rbac.service.hibernate.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.comps.rbac.service.impl.UserManageServiceCommonImpl;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA开发的用户管理接口实现
 *
 * @author : john.peng created on date : 2017/11/10
 */
@Component("userManageServiceHibernate")
public class UserManageServiceImpl extends UserManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(UserManageServiceCommonImpl.class);

    @Autowired
    @Qualifier("generalDictAccessor")
    private GeneralDictAccessor accessor = null;

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#allocateAccount(AccountManageService.AccountInfo)
     */
    @Transactional
    @Override
    public Account allocateAccount(AccountManageService.AccountInfo accountInfo) {
        super.accessor = accessor;
        return super.allocateAccount(accountInfo);
    }

    /**
     * {@inheritDoc}
     *
     * @see UserManageServiceCommonImpl#save(User)
     */
    @Override
    protected User save(User user) {
        return accessor.save(user, false);
    }

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#saveUser(UserManageService.UserInfo)
     */
    @Transactional
    @Override
    public User saveUser(UserInfo userInfo) {
        super.accessor = accessor;
        return super.saveUser(userInfo);
    }
}
