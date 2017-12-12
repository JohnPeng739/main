package org.mx.dal.config;

import org.mx.dal.util.Dbcp2DataSourceFactory;
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
import java.util.HashSet;
import java.util.Set;

/**
 * 基于Hibernate的DAL基础实现
 *
 * @author : john.peng date : 2017/10/7
 * @see DalConfig
 */
@Configuration
@EnableTransactionManagement
@Import(DalConfig.class)
@PropertySource({
        "classpath:database.properties",
        "classpath:jpa.properties"
})
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

    @Bean(name = "dataSourceFactory", initMethod = "init", destroyMethod = "close")
    /**
     * 创建JDBC数据源工厂
     */
    public Dbcp2DataSourceFactory dataSourceFactory() {
        return new Dbcp2DataSourceFactory(env);
    }

    @Bean(name = "dataSource")
    /**
     * 从工厂中获取JDBC数据源
     */
    public DataSource dataSource() {
        return dataSourceFactory().getDataSource();
    }

    @Bean("jpaEntityPackagesDal")
    @Lazy(false)
    public JpaEntityPackagesDefine jpaEntityPackages() {
        return new JpaEntityPackagesDefine("org.mx.dal.entity");
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

        String[] jpaDefines = context.getBeanNamesForType(JpaEntityPackagesDefine.class);
        Set<String> jpaEntityPackages = new HashSet<>();
        for (String jpaDefine : jpaDefines) {
            JpaEntityPackagesDefine define = context.getBean(jpaDefine, JpaEntityPackagesDefine.class);
            jpaEntityPackages.addAll(define.getPackages());
        }
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(context.getBean("dataSource", DataSource.class));
        emf.setPackagesToScan(jpaEntityPackages.toArray(new String[0]));
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
