package org.mx.comps.rbac.config;

import org.mx.dal.config.DalMongodbConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * RBAC组件中相关信息的配置类
 *
 * @author : john.peng created on date : 2017/12/03
 */
@Configuration
@Import(DalMongodbConfig.class)
@ComponentScan({
        "org.mx.comps.rbac.service.impl"
})
public class CompsRbacConfig {
}
