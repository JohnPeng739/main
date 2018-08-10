package org.mx.comps.rbac.config;

import org.mx.comps.rbac.mongodb.config.CompsRbacMongodbConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:mongodb.properties"
})
@Import({
        CompsRbacMongodbConfig.class
})
public class TestMongodbConfig {
}
