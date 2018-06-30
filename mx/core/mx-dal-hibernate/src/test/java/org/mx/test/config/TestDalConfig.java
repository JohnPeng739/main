package org.mx.test.config;

import org.mx.dal.config.DalHibernateConfig;
import org.mx.dal.config.JpaEntityPackagesDefine;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 测试DAL的配置类
 *
 * @author : john.peng created on date : 2017/12/03
 */
@Configuration
@PropertySource({
        "classpath:database.properties",
        "classpath:jpa.properties"
})
@EnableTransactionManagement
@Import(DalHibernateConfig.class)
@ComponentScan({
        "org.mx.test.service.impl"
})
@EnableJpaRepositories({
        "org.mx.test.repository"
})
public class TestDalConfig {
    @Bean("jpaEntityPackagesDalTest")
    @Lazy(false)
    public JpaEntityPackagesDefine jpaEntityPackages() {
        return new JpaEntityPackagesDefine("org.mx.test.entity");
    }
}
