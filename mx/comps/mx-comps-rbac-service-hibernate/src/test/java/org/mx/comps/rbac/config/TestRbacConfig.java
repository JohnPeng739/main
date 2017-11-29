package org.mx.comps.rbac.config;

import org.mx.dal.config.DalHibernateConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Rbac
 *
 * @author : john.peng created on date : 2017/11/28
 */
@Configuration
@Import(DalHibernateConfig.class)
@PropertySource({
        "classpath:database.properties",
        "classpath:jpa.properties"
})
@ComponentScan({"org.mx.comps.rbac.service.impl"})
public class TestRbacConfig {
}
