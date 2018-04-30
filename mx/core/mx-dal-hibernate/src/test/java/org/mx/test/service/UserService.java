package org.mx.test.service;

import org.mx.test.entity.User;

public interface UserService {
    User getUserById(String id);

    User getUserByCode(String code);
}
