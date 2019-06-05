package org.mx.test.config;

import org.mx.dal.config.DalHibernateConfig;
import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 测试DAL的配置类
 *
 * @author : john.peng created on date : 2017/12/03
 */
@Configuration
@PropertySource({
        "classpath:database.properties",
        "classpath:pg-jpa.properties"
})
@EnableTransactionManagement
@Import({DalHibernateConfig.class, SpringConfig.class})
@ComponentScan({
        "org.mx.test.service.impl"
})
@EnableJpaRepositories({
        "org.mx.test.repository"
})
public class TestPgDalConfig {
}
