package org.mx.h2.server.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Server;
import org.mx.StringUtils;
import org.mx.h2.server.H2ServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by john on 2017/10/7.
 */
@Configuration
@PropertySource({"classpath:h2-server.properties"})
public class DatabaseServerConfig {
    private static final Log logger = LogFactory.getLog(DatabaseServerConfig.class);

    @Autowired
    private Environment env = null;

    @Bean(name = "h2ServerFactory", initMethod = "init", destroyMethod = "close")
    public H2ServerFactory h2ServerFactory() {
        return new H2ServerFactory(env);
    }
}
