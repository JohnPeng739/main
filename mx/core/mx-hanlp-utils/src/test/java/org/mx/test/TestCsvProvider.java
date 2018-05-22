package org.mx.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.factory.SuggesterFactory;
import org.mx.test.config.TestCsvConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestCsvProvider {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        // prepare csv file
        String root = System.getProperty("user.dir");
        Path path = Paths.get(root, "src/test/resources/expert.csv");
        try {
            Files.write(path, Arrays.asList("1,威廉王子发表演说 呼吁保护野生动物",
                    "2,《时代》年度人物最终入围名单出炉 普京马云入选", "3,“黑格比”横扫菲：菲吸取“海燕”经验及早疏散",
                    "4,日本保密法将正式生效 日媒指其损害国民知情权", "5,英报告说空气污染带来“公共健康危机”"));
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        context = new AnnotationConfigApplicationContext(TestCsvConfig.class);

        // delete the csv file
        try {
            Files.delete(path);
        } catch (IOException ex) {
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
    public void test() {
        SuggesterFactory factory = context.getBean(SuggesterFactory.class);
        assertNotNull(factory);
        assertNull(factory.getSuggester());
        ItemSuggester itemSuggester = factory.getSuggester("test");
        assertNotNull(itemSuggester);

        while (!factory.ready()) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

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
