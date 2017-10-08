package com.ds.retl.service.impl;

import com.ds.retl.dal.entity.User;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.OperateLogService;
import com.ds.retl.service.UserManageService;
import org.mx.DigestUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

/**
 * Created by john on 2017/10/8.
 */
@Component("userManageServiceHibernate")
public class UserManageServiceImpl implements UserManageService {
    @Autowired
    @Qualifier("generalDictEntityAccessorHibernate")
    private GeneralDictAccessor accessor = null;

    @Autowired
    @Qualifier("operateLogService")
    private OperateLogService logService = null;

    @Override
    public User initUser() throws UserInterfaceErrorException {
        String userCode = "ds110";
        try {
            User user = accessor.getByCode(userCode, User.class);
            if (user == null) {
                user = EntityFactory.createEntity(User.class);
                user.setCode("ds110");
            }
            try {
                user.setPassword(DigestUtils.md5("edmund110119"));
            } catch (NoSuchAlgorithmException ex) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_PASSWORD_DISGEST_FAIL);
            }
            user.setName("RETL管理员");
            user.setRoles("manager");
            return saveUser(user);
        } catch (EntityInstantiationException | EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    @Override
    public User saveUser(User user) throws UserInterfaceErrorException {
        try {
            user = accessor.save(user);
            logService.writeLog(String.format("成功新增了用户[%s]。", user.getCode()));
            return user;
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    @Override
    public User changePassword(String userCode, String oldPassword, String newPassword)
            throws UserInterfaceErrorException {
        try {
            User user = accessor.getByCode(userCode, User.class);
            if (user == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_NOT_FOUND);
            }
            try {
                String digestPwd = DigestUtils.md5(oldPassword);
                if (!digestPwd.equals(user.getPassword())) {
                    throw new UserInterfaceErrorException(UserInterfaceErrors.USER_PASSWORD_UNMATCH);
                }

                digestPwd = DigestUtils.md5(newPassword);
                user.setPassword(digestPwd);
                user = accessor.save(user);
                logService.writeLog(String.format("成功修改了用户[%s]的密码。", userCode));
                return user;
            } catch (NoSuchAlgorithmException ex) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_PASSWORD_DISGEST_FAIL);
            }
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    @Override
    public User login(String userCode, String password) throws UserInterfaceErrorException {
        try {
            User user = accessor.getByCode(userCode, User.class);
            if (user == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_NOT_FOUND);
            }
            try {
                String digestPwd = DigestUtils.md5(password);
                if (digestPwd.equals(user.getPassword())) {
                    // 写入登录成功的日志
                    user.setOnline(true);
                    user = accessor.save(user);
                    logService.writeLog(String.format("用户[%s]成功登录进入系统。", userCode));
                    return user;
                } else {
                    throw new UserInterfaceErrorException(UserInterfaceErrors.USER_PASSWORD_UNMATCH);
                }
            } catch (NoSuchAlgorithmException ex) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_PASSWORD_DISGEST_FAIL);
            }
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    @Override
    public void logout(String userCode) throws UserInterfaceErrorException {
        try {
            User user = accessor.getByCode(userCode, User.class);
            if (user == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.USER_NOT_FOUND);
            }
            user.setOnline(false);
            accessor.save(user);
            logService.writeLog(String.format("用户[%s]成功注销退出系统。", userCode));
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }
}
