package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.comps.rbac.BaseTest;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.service.hibernate.TestLogicalTransactService;

import static org.junit.Assert.*;

public class TestLogicalTransaction extends BaseTest {
    @Test
    public void commit() {
        TestLogicalTransactService service = context.getBean("testLogicalTransactService", TestLogicalTransactService.class);
        assertNotNull(service);

        try {
            assertEquals(0, service.count(User.class));
            assertEquals(0, service.count(Account.class));

            service.commit();

            assertEquals(1, service.count(User.class));
            assertEquals(1, service.count(Account.class));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void rollback() {
        TestLogicalTransactService service = context.getBean("testLogicalTransactService", TestLogicalTransactService.class);
        assertNotNull(service);

        assertEquals(0, service.count(User.class));
        assertEquals(0, service.count(Account.class));
        try {
            service.rollback();
        } catch (Exception ex) {
            // right, do nothing.
        }
        assertEquals(0, service.count(User.class));
        assertEquals(0, service.count(Account.class));
    }
}
