package org.mx.test;

import org.junit.Before;
import org.junit.Test;
import org.mx.dal.EntityFactory;
import org.mx.dal.config.DalHibernateConfig;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralDictEntityAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.test.entity.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * Created by john on 2017/10/7.
 */
public class TestDatabase {
    private ApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(DalHibernateConfig.class);
        SessionDataStore store = context.getBean("sessionDataThreadLocal", SessionDataStore.class);
        store.setCurrentUserCode("SYSTEM");
    }

    @Test
    public void testUserInterface() {
        GeneralDictEntityAccessor accessor = context.getBean("generalDictEntityAccessor",
                GeneralDictEntityAccessor.class);
        assertNotNull(accessor);

        try {
            assertEquals(0, accessor.count(User.class));
            User user = EntityFactory.createEntity(User.class);
            user.setCode("john");
            user.setName("John Peng");
            user.setAddress("address");
            user.setEmail("email");
            user.setPostCode("zip");
            user.setDesc("description");
            User check = accessor.save(user);
            assertNotNull(check);
            assertNotNull(check.getId());
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());
            assertEquals(user.getAddress(), check.getAddress());
            assertTrue(user.getCreatedTime() > 0);
            assertEquals(1, accessor.count(User.class));

            check = accessor.getById(user.getId(), User.class);
            assertNotNull(check);
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());

            check = accessor.getByCode(user.getCode(), User.class);
            assertNotNull(check);
            assertEquals(user.getId(), check.getId());
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());

            user = accessor.getByCode("john", User.class);
            user.setCode("john-1");
            user.setName(user.getName() + "(editable)");
            user.setDesc("I'm John Peng.");
            check = accessor.save(user);
            assertNotNull(check);
            assertEquals(1, accessor.count(User.class));
            assertEquals(user.getId(), check.getId());
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());
            assertEquals(user.getDesc(), check.getDesc());

            check = accessor.getByCode("john", User.class);
            assertNotNull(check);
            check = accessor.getById(user.getId(), User.class);
            assertNotNull(check);
            assertEquals(user.getCode(), check.getCode());

            check = accessor.getByCode("john-1", User.class);
            // 证明修改代码无效
            assertNull(check);

            user = EntityFactory.createEntity(User.class);
            user.setCode("josh");
            user.setName("Josh Peng");
            check = accessor.save(user);
            assertNotNull(check);
            assertEquals(2, accessor.count(User.class));

            check = accessor.getByCode("john", User.class);
            assertNotNull(check);
            assertTrue(check.isValid());
            check = accessor.getByCode("josh", User.class);
            assertNotNull(check);
            assertTrue(check.isValid());

            user = accessor.getByCode("john", User.class);
            assertNotNull(user);
            check = accessor.remove(user);
            assertNotNull(check);
            assertEquals(2, accessor.count(User.class));
            assertFalse(check.isValid());

            user = accessor.getByCode("john", User.class);
            check = accessor.remove(user, false);
            assertNotNull(check);
            assertEquals(1, accessor.count(User.class));
            check = accessor.getById(user.getId(), User.class);
            assertNull(check);
            check = accessor.getByCode(user.getCode(), User.class);
            assertNull(check);
            check = accessor.getByCode("josh", User.class);
            assertNotNull(check);

            user = accessor.getByCode("josh", User.class);
            accessor.remove(user, false);
            assertEquals(0, accessor.count(User.class));
        }catch (EntityAccessException | EntityInstantiationException ex) {
            fail(ex.getMessage());
        }
    }
}
