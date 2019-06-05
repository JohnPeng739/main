package org.mx.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceException;
import org.mx.spring.session.SessionDataStore;
import org.mx.test.config.TestPgDalConfig;
import org.mx.test.pgentity.GeometryEntity;
import org.postgresql.geometric.PGbox;
import org.postgresql.geometric.PGpath;
import org.postgresql.geometric.PGpoint;
import org.postgresql.geometric.PGpolygon;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class TestPgDatabase {
    protected AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestPgDalConfig.class);
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
        CleanH2DatabaseFile.cleanDataFile("./h2/test");
    }

    @Test
    public void testUserInterface() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);

        try {
            accessor.clear(GeometryEntity.class);

            assertEquals(0, accessor.count(GeometryEntity.class));
            GeometryEntity user = EntityFactory.createEntity(GeometryEntity.class);
            user.setBox(new PGbox(new PGpoint(1,1), new PGpoint(100,200)));
            PGpoint[] points = {new PGpoint(1,1), new PGpoint(1,200), new PGpoint(100,200)};
            user.setPath(new PGpath(points, false));
            user.setPoint(new PGpoint(10,200));
            user.setPolygon(new PGpolygon(points));
            GeometryEntity check = accessor.save(user);
            assertNotNull(check);
            assertNotNull(check.getId());
            assertEquals(user.getPath(), check.getPath());
            assertEquals(user.getPoint(), check.getPoint());
            assertEquals(user.getBox(), check.getBox());
            assertTrue(user.getCreatedTime() > 0);
            assertEquals(1, accessor.count(GeometryEntity.class));

            check.setPoint(new PGpoint(1000, 20000));
            GeometryEntity check1 = accessor.save(check);
            assertEquals(user.getPoint(), check.getPoint());
            assertEquals(user.getBox(), check.getBox());
            assertEquals(1, accessor.count(GeometryEntity.class));

            //accessor.clear(GeometryEntity.class);
            //assertEquals(0, accessor.count(GeometryEntity.class));
        } catch (UserInterfaceException ex) {
            fail(ex.getMessage());
        }
    }
}
