package org.mx.dal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.dal.config.TestDalMongodbTransactionConfig;
import org.mx.dal.entity.User;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.TestTransactionService;
import org.mx.error.UserInterfaceException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class TestTransaction {
    protected AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        try {
            context = new AnnotationConfigApplicationContext(TestDalMongodbTransactionConfig.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testTransaction() {
        GeneralAccessor accessor = context.getBean("generalAccessor", GeneralAccessor.class);
        assertNotNull(accessor);
        TestTransactionService service = context.getBean(TestTransactionService.class);
        assertNotNull(service);

        assertEquals(0, accessor.count(User.class));
        service.testCommit();
        assertTrue(service.commitSuccess());

        assertEquals(1, accessor.count(User.class));
        try {
            service.testRollback();
        } catch (UserInterfaceException ex) {
            ex.printStackTrace();
        }
        assertEquals(1, accessor.count(User.class));
        service.rollbackSuccess();
        accessor.clear(User.class);
    }
}
