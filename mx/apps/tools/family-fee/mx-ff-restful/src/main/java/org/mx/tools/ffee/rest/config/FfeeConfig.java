package org.mx.tools.ffee.rest.config;

import org.mx.comps.rbac.RBACRestApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 描述： FFEE应用中Restful服务配置类。
 *
 * @author John.Peng
 *         Date time 2018/2/18 上午11:57
 */
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "org.mx.tools.ffee.repository")
@EnableTransactionManagement
@Import({RBACRestApplication.class})
@PropertySource({"classpath:ffee.properties"})
@ComponentScan({
        "org.mx.tools.ffee.restful",
        "org.mx.tools.ffee.service.impl"
})
public class FfeeConfig {
}
