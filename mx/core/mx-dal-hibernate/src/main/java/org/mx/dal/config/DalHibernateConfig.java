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

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Created by john on 2017/10/7.
 */
@Configuration
@Import(DalConfig.class)
@PropertySource({"classpath:jpa.properties"})
@ComponentScan({"org.mx.service.impl"})
public class DalHibernateConfig {
    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    public DalHibernateConfig() {
        super();
    }

    @Bean
    @DependsOn({"dataSource"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        String database = env.getProperty("jpa.database", String.class, "test");
        boolean generateDDL = env.getProperty("jpa.generateDDL", Boolean.class, true);
        boolean showSQL = env.getProperty("jpa.showSQL", Boolean.class, true);
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.valueOf(database));
        adapter.setGenerateDdl(generateDDL);
        adapter.setShowSql(showSQL);

        String entityPackageString = env.getProperty("jpa.entity.package", String.class, "");
        String[] entityPackages = entityPackageString.split(",");
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(context.getBean("dataSource", DataSource.class));
        emf.setPackagesToScan(entityPackages);
        emf.setJpaVendorAdapter(adapter);
        return emf;
    }

    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory().getObject().createEntityManager();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
