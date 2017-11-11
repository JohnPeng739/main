package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralEntityAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 基于Hibernate JPA开发的用户管理接口实现
 *
 * @author : john.peng created on date : 2017/11/10
 */
@Component
public class UserManageServiceImpl implements UserManageService {
    private static final Log logger = LogFactory.getLog(UserManageServiceImpl.class);

    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralEntityAccessor accessor = null;

    @Override
    public User deleteUser(String id) throws UserInterfaceErrorException {
        try {
            User user = accessor.getById(id, User.class);
            if (user == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_NOT_FOUND);
            }
            user = accessor.remove(user);
            return user;
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Delete user[%s] fail.", id), ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }
}
