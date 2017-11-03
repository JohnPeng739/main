package com.ds.retl.rest.config;

import com.ds.retl.rest.ServerManageResource;
import com.ds.retl.rest.TopologyManageResource;
import com.ds.retl.rest.UserManageResource;
import org.mx.dal.config.DalHibernateConfig;
import org.mx.rest.server.config.ServerConfig;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * RETL REST服务平台应用Java Configure定义
 * 扫描：com.ds.retl.service.impl, com.ds.retl.rest中的组件
 * 加载配置：classpath:deploy.properties, classpath:storm.properties
 * 载入：ServerConfig.class, DalHibernateConfig.class
 *
 * @author : john.peng created on date : 2017/10/6
 * @see ServerConfig
 * @see DalHibernateConfig
 */
@Configuration
@Import({ServerConfig.class, DalHibernateConfig.class})
@PropertySource({"classpath:deploy.properties"})
@PropertySource({"classpath:storm.properties"})
@ComponentScan({
        "com.ds.retl.service.impl",
        "com.ds.retl.rest"
})
public class RETLRestConfig {
    public RETLRestConfig() {
        super();
    }

    /**
     * 创建REST服务列表
     *
     * @return 服务列表
     */
    @Bean(name = "restfulClassesRETL")
    public List<Class<?>> restulClassesRETL() {
        return Arrays.asList(UserManageResource.class, TopologyManageResource.class, ServerManageResource.class);
    }
}
