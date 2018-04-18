package org.mx.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.factory.SuggesterFactory;
import org.mx.test.config.TestCsvConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
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

        List<ItemSuggester.SuggestItem> list = itemSuggester.suggest("发言");
        assertEquals(1, list.size());
        assertEquals("1", list.get(0).getId());
        assertEquals("威廉王子发表演说 呼吁保护野生动物", list.get(0).getContent());
        list = itemSuggester.suggest("危机公共");
        assertEquals(1, list.size());
        assertEquals("5", list.get(0).getId());
        assertEquals("英报告说空气污染带来“公共健康危机”", list.get(0).getContent());
        list = itemSuggester.suggest("may");
        assertEquals(1, list.size());
        assertEquals("2", list.get(0).getId());
        assertEquals("《时代》年度人物最终入围名单出炉 普京马云入选", list.get(0).getContent());

    }
}
