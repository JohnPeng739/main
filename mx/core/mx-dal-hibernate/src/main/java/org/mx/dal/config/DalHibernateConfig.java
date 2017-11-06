package org.mx.dal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * 基于Hibernate的DAL基础实现
 *
 * @author : john.peng date : 2017/10/7
 * @see DalConfig
 */
@Configuration
@EnableTransactionManagement
@Import(DalConfig.class)
@PropertySource({"classpath:jpa.properties"})
@ComponentScan({"org.mx.dal.service.impl"})
public class DalHibernateConfig implements TransactionManagementConfigurer {
    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    /**
     * 默认的构造函数
     */
    public DalHibernateConfig() {
        super();
    }

    /**
     * 创建实体管理器工厂Bean
     *
     * @return 实体管理器工厂Bean
     */
    @Bean
    @DependsOn({"dataSource"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        String database = env.getProperty("jpa.database", String.class, "H2");
        String databasePlatform = env.getProperty("jpa.databasePlatform", String.class,
                "org.hibernate.dialect.H2Dialect");
        boolean generateDDL = env.getProperty("jpa.generateDDL", Boolean.class, true);
        boolean showSQL = env.getProperty("jpa.showSQL", Boolean.class, true);
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.valueOf(database));
        adapter.setDatabasePlatform(databasePlatform);
        adapter.setGenerateDdl(generateDDL);
        adapter.setShowSql(showSQL);
        adapter.setPrepareConnection(true);

        String entityPackageString = env.getProperty("jpa.entity.package", String.class, "");
        String[] entityPackages = entityPackageString.split(",");
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(context.getBean("dataSource", DataSource.class));
        emf.setPackagesToScan(entityPackages);
        emf.setJpaVendorAdapter(adapter);
        return emf;
    }

    /**
     * 创建事务管理器
     *
     * @return 事务管理器
     */
    @Bean()
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return transactionManager;
    }

    /**
     * 创建注解驱动的事务管理器
     *
     * @return 事务管理器
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }
}
