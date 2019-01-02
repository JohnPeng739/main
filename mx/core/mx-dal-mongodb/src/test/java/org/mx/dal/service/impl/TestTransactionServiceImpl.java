package org.mx.dal.service.impl;

import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.User;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.TestTransactionService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestTransactionServiceImpl implements TestTransactionService {
    private GeneralDictAccessor generalDictAccessor;

    @Autowired
    public TestTransactionServiceImpl(@Qualifier("generalDictAccessorMongodb") GeneralDictAccessor generalDictAccessor) {
        super();
        this.generalDictAccessor = generalDictAccessor;
    }

    @Transactional
    @Override
    public void testCommit() {
        User user = EntityFactory.createEntity(User.class);
        user.setCode("john");
        user.setName("John.Peng");
        user.setMobile("12345678901");
        user = generalDictAccessor.save(user);

        // 修改操作，同一个事务
        user = generalDictAccessor.getById(user.getId(), User.class);
        user.setMobile("09876543210");
        generalDictAccessor.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean commitSuccess() {
        User user = generalDictAccessor.getByCode("john", User.class);
        return user != null && "09876543210".equals(user.getMobile());
    }

    @Transactional
    @Override
    public void testRollback() {
        User user = EntityFactory.createEntity(User.class);
        user.setCode("josh");
        user.setName("Josh.Peng");
        user.setMobile("12345678901");
        user = generalDictAccessor.save(user);

        if (!StringUtils.isBlank(user.getId())) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }

        // 修改操作，同一个事务
        user = generalDictAccessor.getById(user.getId(), User.class);
        user.setMobile("09876543210");
        generalDictAccessor.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean rollbackSuccess() {
        User user = generalDictAccessor.getByCode("josh", User.class);
        return user == null;
    }
}
