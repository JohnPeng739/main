package org.mx.dal.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.test.config.TestAllDalConfig;
import org.mx.dal.test.entity.School;
import org.mx.dal.test.entity.SchoolElasticEntity;
import org.mx.dal.test.entity.SchoolJpaEntity;
import org.mx.dal.test.entity.SchoolMongodbEntity;
import org.mx.spring.session.SessionDataStore;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class TestDal {
    private AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestAllDalConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testJpa() {
        GeneralAccessor jpa = context.getBean("generalAccessor", GeneralAccessor.class);
        assertNotNull(jpa);
        SessionDataStore sessionDataStore = context.getBean(SessionDataStore.class);
        assertNotNull(sessionDataStore);

        sessionDataStore.setCurrentUserCode("System");

        jpa.clear(SchoolJpaEntity.class);
        assertEquals(0, jpa.count(SchoolJpaEntity.class));

        SchoolJpaEntity school = EntityFactory.createEntity(SchoolJpaEntity.class);
        school.setName("jpa");
        school.setLocation(new double[] {1.1234, 9.98765});
        SchoolJpaEntity check = jpa.save(school);
        assertNotNull(check);
        assertFalse(StringUtils.isBlank(school.getId()));
        assertFalse(StringUtils.isBlank(check.getId()));
        assertEquals(check.getName(), school.getName());

        check = jpa.getById(school.getId(), SchoolJpaEntity.class);
        assertNotNull(check);
        assertEquals("jpa", check.getName());

        jpa.clear(SchoolJpaEntity.class);
        assertEquals(0, jpa.count(SchoolJpaEntity.class));
    }

    @Test
    public void testMongodb() {
        GeneralAccessor mongodb = context.getBean("generalAccessorMongodb", GeneralAccessor.class);
        assertNotNull(mongodb);

        mongodb.clear(SchoolMongodbEntity.class);
        assertEquals(0, mongodb.count(SchoolMongodbEntity.class));

        SchoolMongodbEntity school = EntityFactory.createEntity(SchoolMongodbEntity.class);
        school.setName("mongodb");
        school.setLocation(new double[] {1.1234, 9.98765});
        SchoolMongodbEntity check = mongodb.save(school);
        assertNotNull(check);
        assertFalse(StringUtils.isBlank(school.getId()));
        assertFalse(StringUtils.isBlank(check.getId()));
        assertEquals(check.getName(), school.getName());

        check = mongodb.getById(school.getId(), SchoolMongodbEntity.class);
        assertNotNull(check);
        assertEquals("mongodb", check.getName());

        mongodb.clear(School.class);
        assertEquals(0, mongodb.count(SchoolMongodbEntity.class));
    }

    @Test
    public void testElastic() {
        GeneralAccessor elastic = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(elastic);

        assertEquals(0, elastic.count(SchoolElasticEntity.class));

        SchoolElasticEntity school = EntityFactory.createEntity(SchoolElasticEntity.class);
        school.setName("elastic");
        school.setLocation(new double[] {1.1234, 9.98765});
        SchoolElasticEntity check = elastic.save(school);
        assertNotNull(check);
        assertFalse(StringUtils.isBlank(school.getId()));
        assertFalse(StringUtils.isBlank(check.getId()));
        assertEquals(check.getName(), school.getName());

        check = elastic.getById(school.getId(), SchoolElasticEntity.class);
        assertNotNull(check);
        assertEquals("elastic", check.getName());

        elastic.clear(SchoolElasticEntity.class);
    }

    @Test
    public void testAll() {
        testJpa();
        testElastic();
        testMongodb();
    }
}
