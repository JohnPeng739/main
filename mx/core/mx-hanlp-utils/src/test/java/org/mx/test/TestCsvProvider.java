package org.mx.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.factory.SuggesterFactory;
import org.mx.test.config.TestCsvConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestCsvProvider {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestCsvConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() {
        SuggesterFactory factory = context.getBean(SuggesterFactory.class);
        assertNotNull(factory);
        assertNull(factory.getSuggester());
        ItemSuggester itemSuggester = factory.getSuggester("expert from text");
        assertNotNull(itemSuggester);

        Set<ItemSuggester.SuggestItem> set = itemSuggester.suggest("发言");
        assertEquals(1, set.size());
        set = itemSuggester.suggest("危机公共");
        assertEquals(1, set.size());
        set = itemSuggester.suggest("may");
        assertEquals(1, set.size());

        // TODO 测试具体内容，并将接口中的set修改为list类型。
    }
}
