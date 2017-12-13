package org.mx.comps.rbac.rest.config;

import org.mx.comps.rbac.config.CompsRbacHibernateConfig;
import org.mx.comps.rbac.config.CompsRbacMongodbConfig;
import org.mx.comps.rbac.rest.*;
import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

/**
 * 基于MongoDB实现的RBAC微服务配置定义
 *
 * @author : john.peng created on date : 2017/12/13
 */
@Configuration
@Import({RbacRestConfig.class, CompsRbacMongodbConfig.class})
public class RbacRestMongodbConfig {
}
