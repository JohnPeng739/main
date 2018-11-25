package org.mx.service.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.rest.graphql.GraphQLConfigBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestGraphQLConfig {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(org.mx.service.test.config.TestGraphQLConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() {
        GraphQLConfigBean config = context.getBean(GraphQLConfigBean.class);
        assertNotNull(config);

        List<GraphQLConfigBean.GraphQLItem> items = config.getConfig();
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("key1", items.get(0).getKey());
        assertEquals("/key1.graphql", items.get(0).getSchemaPath());
        assertEquals(GraphQLConfigBean.PathType.CLASS_PATH, items.get(0).getPathType());
        assertEquals("key2", items.get(1).getKey());
        assertEquals("/key2.graphql", items.get(1).getSchemaPath());
        assertEquals(GraphQLConfigBean.PathType.FILE_PATH, items.get(1).getPathType());
    }
}
