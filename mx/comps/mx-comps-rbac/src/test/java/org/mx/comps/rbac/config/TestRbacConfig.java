package org.mx.comps.rbac.config;

import org.mx.comps.rbac.rest.config.RbacRestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RbacRestConfig.class})
public class TestRbacConfig {
}
