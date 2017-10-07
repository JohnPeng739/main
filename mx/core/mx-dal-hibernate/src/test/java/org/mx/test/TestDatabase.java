package org.mx.test;

import org.junit.Before;
import org.junit.Test;
import org.mx.dal.config.DalHibernateConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;

/**
 * Created by john on 2017/10/7.
 */
public class TestDatabase {
    private ApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(DalHibernateConfig.class);
    }

    @Test
    public void testConnection() {
        DataSource dataSource = context.getBean("dataSource", DataSource.class);
        assertNotNull(dataSource);
        EntityManager entityManager = context.getBean(EntityManager.class);
        assertNotNull(entityManager);
    }
}
