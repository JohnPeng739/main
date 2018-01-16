package org.mx.comps.rbac.rest.config;

import org.mx.comps.rbac.rest.*;
import org.mx.service.server.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * RBAC微服务配置定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Configuration
@EnableAspectJAutoProxy
@Import({ServerConfig.class})
@ComponentScan({
        "org.mx.comps.rbac.rest",
        "org.mx.comps.jwt"
})
public class RbacRestConfig {
    @Autowired
    private ApplicationContext context = null;

    @Bean(name = "restfulClassesRBAC")
    public List<Class<?>> rbacRestfulClasses() {
        return Arrays.asList(UserManageResource.class, AccountManageResource.class, RoleManageResource.class,
                DepartmentManageResource.class, PrivilegeManageResource.class, AccreditManageResource.class);
    }
}
