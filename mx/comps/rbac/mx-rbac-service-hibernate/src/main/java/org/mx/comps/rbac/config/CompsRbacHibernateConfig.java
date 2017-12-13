package org.mx.comps.rbac.config;

import org.mx.dal.config.DalHibernateConfig;
import org.mx.dal.config.JpaEntityPackagesDefine;
import org.springframework.context.annotation.*;

/**
 * RBAC组件中相关信息的配置类
 *
 * @author : john.peng created on date : 2017/12/03
 */
@Configuration
@Import(DalHibernateConfig.class)
@ComponentScan({
        "org.mx.comps.rbac.service.hibernate.impl"
})
public class CompsRbacHibernateConfig {
    @Bean("jpaEntityPackagesRbac")
    @Lazy(false)
    public JpaEntityPackagesDefine jpaEntityPackages() {
        return new JpaEntityPackagesDefine("org.mx.comps.rbac.dal.entity");
    }
}
