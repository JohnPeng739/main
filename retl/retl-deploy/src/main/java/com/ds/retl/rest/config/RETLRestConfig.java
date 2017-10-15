package com.ds.retl.rest.config;

import com.ds.retl.rest.TopologyManageResource;
import com.ds.retl.rest.UserManageResource;
import org.mx.dal.config.DalHibernateConfig;
import org.mx.rest.server.config.ServerConfig;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/10/6.
 */
@Configuration
@Import({ServerConfig.class, DalHibernateConfig.class})
@PropertySource({"classpath:/deploy.properties"})
@ComponentScan({
        "com.ds.retl.service.impl",
        "com.ds.retl.rest"
})
public class RETLRestConfig {
    public RETLRestConfig() {
        super();
    }

    @Bean(name = "restfulClassesRETL")
    public List<Class<?>> restulClassesRETL() {
        return Arrays.asList(UserManageResource.class, TopologyManageResource.class);
    }
}
