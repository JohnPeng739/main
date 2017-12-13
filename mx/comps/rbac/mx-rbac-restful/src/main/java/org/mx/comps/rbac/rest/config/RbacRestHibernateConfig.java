package org.mx.comps.rbac.rest.config;

import org.mx.comps.rbac.config.CompsRbacHibernateConfig;
import org.mx.comps.rbac.rest.*;
import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

/**
 * 基于Hibernate实现的RBAC微服务配置定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Configuration
@Import({RbacRestConfig.class, CompsRbacHibernateConfig.class})
public class RbacRestHibernateConfig {
}
