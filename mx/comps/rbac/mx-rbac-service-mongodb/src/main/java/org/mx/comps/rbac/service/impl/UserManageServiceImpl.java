package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.util.StringUtil;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Department;
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
    @Qualifier("generalDictAccessor")
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
     * @see UserManageServiceCommonImpl#save(User)
     */
    @Override
    protected User save(User user) {
        Department oldDepart= null;
        if (!StringUtil.isBlank(user.getId())) {
            oldDepart = accessor.getById(user.getId(), User.class).getDepartment();
        }
        user = accessor.save(user, false);
        Department depart = user.getDepartment();
        if (oldDepart != null && !oldDepart.equals(depart)) {
            // 与原有的部门不同，因此需要删除原有的部门和用户关系
            oldDepart.getEmployees().remove(user);
            accessor.save(oldDepart, false);
        }
        if (depart != null && !depart.equals(oldDepart)) {
            // 新设置了部门
            depart.getEmployees().add(user);
            accessor.save(depart, false);
        }
        return user;
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
