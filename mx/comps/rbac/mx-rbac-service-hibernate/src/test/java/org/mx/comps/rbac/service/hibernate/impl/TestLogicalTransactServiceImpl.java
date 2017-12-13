package org.mx.comps.rbac.service.hibernate.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.hibernate.TestLogicalTransactService;
import org.mx.dal.EntityFactory;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.impl.GeneralDictEntityAccessorImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("testLogicalTransactService")
public class TestLogicalTransactServiceImpl extends GeneralDictEntityAccessorImpl implements TestLogicalTransactService {
    private static final Log logger = LogFactory.getLog(TestLogicalTransactServiceImpl.class);

    @Transactional
    @Override
    public void rollback() throws UserInterfaceRbacErrorException {
        saveUser();
        saveAccount();
        throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OTHER_FAIL);
    }

    @Transactional
    private void saveUser() {
        User user = EntityFactory.createEntity(User.class);
        user.setFirstName("john");
        user.setLastName("peng");
        user.setSex(User.Sex.FEMALE);
        user.setDesc("description");
        user.setStation("manager");
        super.save(user);
    }

    @Transactional
    private void saveAccount() throws UserInterfaceRbacErrorException {
        Account account = EntityFactory.createEntity(Account.class);
        account.setCode("account");
        account.setName("account");
        account.setPassword("password");
        super.save(account);
    }

    @Transactional
    @Override
    public void commit() throws UserInterfaceRbacErrorException {
        saveUser();
        saveAccount();
    }
}
