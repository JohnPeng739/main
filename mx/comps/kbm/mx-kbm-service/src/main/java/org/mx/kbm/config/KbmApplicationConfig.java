package org.mx.kbm.config;

import org.mx.comps.rbac.jpa.config.CompsRbacHibernateConfig;
import org.mx.service.server.config.ServerConfig;
import org.mx.spring.config.SpringCacheConfig;
import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 描述： 知识库服务配置类
 *
 * @author john peng
 * Date time 2018/5/6 下午2:07
 */
@Configuration
@Import({
        SpringConfig.class,
        SpringCacheConfig.class,
        ServerConfig.class,
        CompsRbacHibernateConfig.class
})
@ComponentScan({
        "org.mx.kbm.service.impl",
        "org.mx.kbm.rest"
})
@EnableJpaRepositories({
        "org.mx.kbm.repository"
})
public class KbmApplicationConfig {
}
