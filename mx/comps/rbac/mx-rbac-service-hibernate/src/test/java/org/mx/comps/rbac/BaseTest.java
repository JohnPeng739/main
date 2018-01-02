package org.mx.comps.rbac;

import org.junit.After;
import org.junit.Before;
import org.mx.comps.rbac.jpa.config.CompsRbacHibernateConfig;
import org.mx.dal.session.SessionDataStore;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertNotNull;

public class BaseTest {
    protected AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        CleanH2DatabaseFile.cleanDataFile("./h2/rbac");
        context = new AnnotationConfigApplicationContext(CompsRbacHibernateConfig.class);
        assertNotNull(context);

        SessionDataStore sessionDataStore = context.getBean(SessionDataStore.class);
        assertNotNull(sessionDataStore);
        sessionDataStore.setCurrentUserCode("admin");
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
        CleanH2DatabaseFile.cleanDataFile("./h2/rbac");
    }
}
