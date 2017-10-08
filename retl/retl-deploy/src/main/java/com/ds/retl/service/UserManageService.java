package com.ds.retl.service;

import com.ds.retl.dal.entity.User;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import org.mx.rest.error.UserInterfaceException;

/**
 * Created by john on 2017/10/8.
 */
public interface UserManageService {
    User saveUser(User user) throws UserInterfaceErrorException;

    User changePassword(String userCode, String password) throws UserInterfaceErrorException;

    User login(String userCode, String password) throws UserInterfaceErrorException;

    void logout(String userCode) throws UserInterfaceErrorException;
}
