package org.mx.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.hanlp.factory.SuggesterConfigBean;
import org.mx.test.config.TestSuggesterConfigConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 描述： 测试推荐配置
 *
 * @author john peng
 * Date time 2018/7/20 下午3:30
 */
public class TestSuggesterConfig {
    private AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestSuggesterConfigConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() {
        SuggesterConfigBean config = context.getBean(SuggesterConfigBean.class);
        assertNotNull(config);

        assertEquals(2, config.getNum());
        assertEquals(2, config.getSuggesterProviders().size());

        assertNotNull(config.getSuggesterProviders().get(0));
        SuggesterConfigBean.SuggesterCsvProviderConfig csvProviderConfig =
                (SuggesterConfigBean.SuggesterCsvProviderConfig)config.getSuggesterProviders().get(0);
        assertEquals("csv", csvProviderConfig.getType());
        assertEquals("test", csvProviderConfig.getName());
        assertEquals("org.mx.hanlp.factory.suggest.CsvFileProvider", csvProviderConfig.getProvider());
        assertEquals(String.format("%s/src/test/resources/expert.csv", System.getProperty("user.dir")),
                csvProviderConfig.getPath());
        assertEquals(1, csvProviderConfig.getIdField());
        assertEquals(2, csvProviderConfig.getContentField());

        assertNotNull(config.getSuggesterProviders().get(1));
        SuggesterConfigBean.SuggesterJdbcProviderConfig jdbcProviderConfig =
                (SuggesterConfigBean.SuggesterJdbcProviderConfig)config.getSuggesterProviders().get(1);
        assertEquals("jdbc", jdbcProviderConfig.getType());
        assertEquals("dangerous goods from db", jdbcProviderConfig.getName());
        assertEquals("org.mx.hanlp.factory.suggest.JdbcProvider", jdbcProviderConfig.getProvider());
        assertEquals("jdbc:oracle:thin:@220.189.211.75:1521:DSDB", jdbcProviderConfig.getUrl());
        assertEquals("oracle.jdbc.driver.OracleDriver", jdbcProviderConfig.getDriver());
        assertEquals("DS_COC_FIRE", jdbcProviderConfig.getUser());
        assertEquals("DS_COC_FIRE", jdbcProviderConfig.getPassword());
        assertEquals("WHP_NM", jdbcProviderConfig.getIdField());
        assertEquals("SELECT WHP_NM,WHP_ZWMC,WHP_YWMC,WHP_WGXZ,WHP_ZYYT,WHP_RJX,WHP_SSX,WHP_WXTX,WHP_JJW,WHP_MHFF,WHP_WXXLB,WHP_CYZYSX,WHP_DX,WHP_JKWH,WHP_PFJCWH,WHP_YJJCWH,WHP_XRWH,WHP_SRWH,WHP_GCKZ,WHP_HXXTFH,WHP_YJFH,WHP_FHF,WHP_SFH,WHP_BMJCTJ,WHP_XLCZ,WHP_ZYSX FROM T_COC_WHPZS", jdbcProviderConfig.getQuery());
    }
}
