package org.mx.comps.rbac.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Hibernate JPA开发的用户管理接口实现
 *
 * @author : john.peng created on date : 2017/11/10
 */
@Component
public class UserManageServiceImpl extends GeneralDictEntityAccessorImpl implements UserManageService {
    private static final Log logger = LogFactory.getLog(UserManageServiceImpl.class);

    @Autowired
    private OperateLogService operateLogService = null;

    @Override
    public User save(String userId, User user) throws UserInterfaceErrorException {
        try {
            User checked = super.getById(userId, User.class);
            if (checked == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The User entity[%s] not found.", userId));
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_NOT_FOUND);
            }
            checked.setBirthday(user.getBirthday());
            checked.setDepartment(user.getDepartment());
            checked.setDesc(user.getDesc());
            checked.setFirstName(user.getFirstName());
            checked.setMiddleName(user.getMiddleName());
            checked.setLastName(user.getLastName());
            checked.setSex(user.getSex());
            checked.setStation(user.getStation());
            checked.setSubordinates(user.getSubordinates());
            user = super.save(checked);
            if (operateLogService != null) {
                operateLogService.writeLog(String.format("修改用户[name=%s]信息成功。", user.getFullName()));
            }
            return user;
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }
}
