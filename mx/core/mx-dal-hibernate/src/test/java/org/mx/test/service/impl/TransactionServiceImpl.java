package org.mx.test.service.impl;

import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.test.entity.User;
import org.mx.test.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionServiceImpl implements TransactionService {
    private GeneralDictAccessor accessor;

    @Autowired
    public TransactionServiceImpl(@Qualifier("generalDictAccessor")GeneralDictAccessor generalDictAccessor) {
        super();
        this.accessor = generalDictAccessor;
    }

    @Transactional
    @Override
    public void commit() {
        User user = EntityFactory.createEntity(User.class);
        user.setCode("john");
        user.setName("John.Peng");
        user.setMobile("12345678901");
        accessor.save(user);
        user = accessor.getById(user.getId(), User.class);
        if (user == null || !"12345678901".equals(user.getMobile())) {
            throw new UserInterfaceDalErrorException(
                    UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL
            );
        }

        user.setMobile("09876543210");
        user.setDesc("Any description.");
        accessor.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean commitSuccess() {
        User user = accessor.getByCode("john", User.class);
        return user != null && "09876543210".equals(user.getMobile()) && !StringUtils.isBlank(user.getDesc());
    }

    @Transactional
    @Override
    public void rollback() {
        User user = EntityFactory.createEntity(User.class);
        user.setCode("josh");
        user.setName("Josh.Peng");
        user.setMobile("12345678901");
        accessor.save(user);
        user = accessor.getById(user.getId(), User.class);
        if (user != null) {
            throw new UserInterfaceDalErrorException();
        }

        user = EntityFactory.createEntity(User.class);
        user.setCode("josh");
        user.setName("Josh.Peng");
        user.setMobile("09876543d210");
        user.setDesc("Any description.");
        accessor.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean rollbackSuccess() {
        User user = accessor.getByCode("josh", User.class);
        return user == null;
    }
}
