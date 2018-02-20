package org.mx.tools.ffee.rest.config;

import org.mx.comps.rbac.rest.config.RbacRestConfig;
import org.mx.comps.rbac.rest.tasks.InitializeAdminAccountTask;
import org.mx.spring.InitializeTask;
import org.mx.tools.ffee.rest.FfeeAccountManageResource;
import org.mx.tools.ffee.rest.FfeeFamilyManageResource;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.List;

/**
 * 描述： FFEE应用中Restful服务配置类。
 *
 * @author John.Peng
 *         Date time 2018/2/18 上午11:57
 */
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "org.mx.tools.ffee.repository")
@EnableTransactionManagement
@Import({RbacRestConfig.class})
@PropertySource({"classpath:ffee.properties"})
@ComponentScan({
        "org.mx.tools.ffee.restful",
        "org.mx.tools.ffee.service.impl"
})
public class FfeeConfig {
    @Bean(name = "initializeTasks")
    public List<Class<? extends InitializeTask>> initializeTasks() {
        return Arrays.asList(InitializeAdminAccountTask.class);
    }

    @Bean(name = "restfulClassesFFEE")
    public List<Class<?>> ffeeRestfulClasses() {
        return Arrays.asList(FfeeAccountManageResource.class, FfeeFamilyManageResource.class);
    }
}
