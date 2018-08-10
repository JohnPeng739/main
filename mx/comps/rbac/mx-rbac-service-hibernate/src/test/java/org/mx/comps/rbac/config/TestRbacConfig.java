package org.mx.comps.rbac.config;

import org.mx.comps.rbac.jpa.config.CompsRbacHibernateConfig;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource({
        "classpath:database.properties",
        "classpath:jpa.properties"
})
@Import({
        CompsRbacHibernateConfig.class
})
public class TestRbacConfig {
}
