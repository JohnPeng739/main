package org.mx.dal.test.config;

import org.mx.dal.config.DalConfig;
import org.mx.dal.config.DalElasticConfig;
import org.mx.dal.config.DalHibernateConfig;
import org.mx.dal.config.DalMongodbConfig;
import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:database.properties",
        "classpath:jpa.properties",
        "classpath:mongodb.properties",
        "classpath:elastic.properties"
})
@Import({
        SpringConfig.class,
        DalConfig.class,
        DalHibernateConfig.class,
        DalMongodbConfig.class,
        DalElasticConfig.class
})
public class TestAllDalConfig {
}
