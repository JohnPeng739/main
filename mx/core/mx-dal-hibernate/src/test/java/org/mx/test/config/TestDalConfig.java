package org.mx.test.config;

import org.mx.dal.config.DalHibernateConfig;
import org.mx.dal.config.JpaEntityPackagesDefine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 测试DAL的配置类
 *
 * @author : john.peng created on date : 2017/12/03
 */
@EnableTransactionManagement
@Import(DalHibernateConfig.class)
public class TestDalConfig {
    @Bean("jpaEntityPackagesDalTest")
    @Lazy(false)
    public JpaEntityPackagesDefine jpaEntityPackages() {
        return new JpaEntityPackagesDefine("org.mx.test.entity");
    }
}
