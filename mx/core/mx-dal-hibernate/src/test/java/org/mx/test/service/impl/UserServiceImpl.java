package org.mx.test.service.impl;

import org.mx.dal.service.GeneralDictAccessor;
import org.mx.test.entity.User;
import org.mx.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    @Qualifier("generalDictAccessor")
    private GeneralDictAccessor dictAccessor;

    @Transactional(readOnly = true)
    @Override
    public User getUserById(String id) {
        User user = dictAccessor.getById(id, User.class);
        if (user.getChildren() != null) {
            user.getChildren().size();
        }
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByCode(String code) {
        User user = dictAccessor.getByCode(code, User.class);
        if (user.getChildren() != null) {
            user.getChildren().size();
        }
        return user;
    }
}
