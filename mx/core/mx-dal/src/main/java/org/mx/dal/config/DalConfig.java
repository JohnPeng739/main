package org.mx.dal.config;

import org.mx.dal.util.Dbcp2DataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Created by john on 2017/10/7.
 */
@Configuration
@PropertySource({"classpath:database.properties"})
@ComponentScan({
        "org.mx.dal.session.impl"
})
public class DalConfig {
    @Autowired
    private Environment env = null;

    public DalConfig() {
        super();
    }

    @Bean(name = "dataSourceFactory", initMethod = "init", destroyMethod = "close")
    public Dbcp2DataSourceFactory dataSourceFactory() {
        return new Dbcp2DataSourceFactory(env);
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        return dataSourceFactory().getDataSource();
    }
}
