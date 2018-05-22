package org.mx.dal.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralAccessorImpl;
import org.mx.dal.service.impl.GeneralDictAccessorImpl;
import org.mx.dal.service.impl.OperateLogServiceImpl;
import org.mx.dal.session.SessionDataStore;
import org.mx.dbcp.Dbcp2DataSourceFactory;
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
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * 基于Hibernate的DAL基础实现
 *
 * @author : john.peng date : 2017/10/7
 * @see DalConfig
 */
@Configuration
@Import(DalConfig.class)
@PropertySource({
        "classpath:database.properties",
        "classpath:jpa.properties"
})
@EnableTransactionManagement
public class DalHibernateConfig implements TransactionManagementConfigurer {
    private static final Log logger = LogFactory.getLog(DalHibernateConfig.class);

    private PlatformTransactionManager transactionManager = null;

    @Bean(name = "generalAccessorJpa")
    public GeneralAccessor generalAccessorJpa(SessionDataStore sessionDataStore) {
        return new GeneralAccessorImpl(sessionDataStore);
    }

    @Bean(name = "generalDictAccessorJpa")
    public GeneralDictAccessor generalDictAccessorJpa(SessionDataStore sessionDataStore) {
        return new GeneralDictAccessorImpl(sessionDataStore);
    }

    @Bean(name = "operateLogService")
    public OperateLogService operateLogService(SessionDataStore sessionDataStore) {
        return new OperateLogServiceImpl(generalAccessorJpa(sessionDataStore), sessionDataStore);
    }

    /**
     * 创建一个通用的数据访问器
     *
     * @return 数据访问器
     */
    @Bean(name = "generalAccessor")
    public GeneralAccessor generalAccessor(ApplicationContext context) {
        return context.getBean("generalAccessorJpa", GeneralAccessor.class);
    }

    /**
     * 创建一个通用的字典数据访问器
     *
     * @return 数据访问器
     */
    @Bean(name = "generalDictAccessor")
    public GeneralDictAccessor generalDictAccessor(ApplicationContext context) {
        return context.getBean("generalDictAccessorJpa", GeneralDictAccessor.class);
    }


    /**
     * 创建JDBC数据源工厂
     */
    @Bean(name = "dataSourceFactory", initMethod = "init", destroyMethod = "close")
    public Dbcp2DataSourceFactory dataSourceFactory(Environment env) {
        Dbcp2DataSourceFactory factory = new Dbcp2DataSourceFactory(env);
        try {
            factory.init();
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Init DB connection pool fail.", ex);
            }
        }
        return factory;
    }


    /**
     * 从工厂中获取JDBC数据源
     */
    @Bean(name = "dataSource")
    public DataSource dataSource(Environment env) {
        return dataSourceFactory(env).getDataSource();
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
    @Bean("entityManagerFactory")
    @DependsOn({"dataSource"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(Environment env,
                                                                           ApplicationContext context) {
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
    @Bean
    public PlatformTransactionManager transactionManager(Environment env, ApplicationContext context) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean(env, context).getObject());
        this.transactionManager = transactionManager;
        return transactionManager;
    }

    /**
     * 创建注解驱动的事务管理器
     *
     * @return 事务管理器
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }
}
