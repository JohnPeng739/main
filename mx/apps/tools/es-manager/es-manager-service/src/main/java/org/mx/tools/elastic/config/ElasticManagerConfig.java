package org.mx.tools.elastic.config;

import org.mx.dal.config.DalElasticConfig;
import org.mx.jwt.config.JwtServiceConfig;
import org.mx.service.server.config.ServerConfig;
import org.mx.tools.elastic.restful.IndexManageResource;
import org.mx.tools.elastic.restful.SystemManageResource;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 描述： ES管理器配置类定义
 *
 * @author john peng
 * Date time 2018/8/31 下午8:19
 */
@Configuration
@PropertySource({
        "classpath:auth.properties",
        "classpath:server.properties",
        "classpath:elastic.properties"
})
@Import({
        JwtServiceConfig.class,
        ServerConfig.class,
        DalElasticConfig.class
})
@ComponentScan({
        "org.mx.tools.elastic.service.impl",
        "org.mx.tools.elastic.restful"
})
public class ElasticManagerConfig {
    @Bean("restfulClassesEsManager")
    public List<Class<?>> restfulClassesEsManager() {
        return Arrays.asList(SystemManageResource.class, IndexManageResource.class);
    }
}
