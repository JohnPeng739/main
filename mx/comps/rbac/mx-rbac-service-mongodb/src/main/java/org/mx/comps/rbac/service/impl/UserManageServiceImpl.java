package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 基于Hibernate JPA开发的用户管理接口实现
 *
 * @author : john.peng created on date : 2017/11/10
 */
@Component("userManageService")
public class UserManageServiceImpl extends UserManageServiceCommonImpl {
    private static final Log logger = LogFactory.getLog(UserManageServiceCommonImpl.class);

    @Autowired
    @Qualifier("generalDictEntityAccessorMongodb")
    private GeneralDictAccessor accessor = null;

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#allocateAccount(AccountManageService.AccountInfo)
     */
    @Override
    public Account allocateAccount(AccountManageService.AccountInfo accountInfo) {
        super.accessor = accessor;
        return super.allocateAccount(accountInfo);
    }

    /**
     * {@inheritDoc}
     *
     * @see UserManageService#saveUser(UserInfo)
     */
    @Override
    public User saveUser(UserInfo userInfo) {
        super.accessor = accessor;
        return super.saveUser(userInfo);
    }
}
