package org.mx.dal.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.impl.GeneralAccessorImpl;
import org.mx.dal.service.impl.GeneralDictAccessorImpl;
import org.mx.dbcp.Dbcp2DataSourceConfigBean;
import org.mx.dbcp.Dbcp2DataSourceFactory;
import org.mx.spring.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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

/**
 * 基于Hibernate的DAL基础实现
 *
 * @author : john.peng date : 2017/10/7
 * @see DalConfig
 */
@Import(DalConfig.class)
@EnableTransactionManagement
public class DalHibernateConfig implements TransactionManagementConfigurer {
    private static final Log logger = LogFactory.getLog(DalHibernateConfig.class);

    private PlatformTransactionManager transactionManager = null;

    @Bean("dbcp2DataSourceConfigBean")
    public DataSourceConfigBean dbcp2DataSourceConfigBean(Environment env) {
        return new Dbcp2DataSourceConfigBean(env, "db");
    }

    @Bean("jpaConfigBean")
    public JpaConfigBean jpaConfigBean() {
        return new JpaConfigBean();
    }

    /**
     * 创建JDBC数据源工厂
     *
     * @param dataSourceConfigBean 数据源配置对象
     * @return DBCP数据源工厂
     */
    @Bean(name = "dataSourceFactory", initMethod = "init", destroyMethod = "close")
    public Dbcp2DataSourceFactory dataSourceFactory(
            @Qualifier("dbcp2DataSourceConfigBean") DataSourceConfigBean dataSourceConfigBean) {
        Dbcp2DataSourceFactory factory = new Dbcp2DataSourceFactory(dataSourceConfigBean);
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
     *
     * @param dataSourceFactory DBCP2数据库连接池
     * @return 数据源
     */
    @Bean(name = "dataSource")
    public DataSource dataSource(Dbcp2DataSourceFactory dataSourceFactory) {
        return dataSourceFactory.getDataSource();
    }

    /**
     * 创建实体管理器工厂Bean
     *
     * @param context       Spring IoC上下文
     * @param dataSource    数据源
     * @param jpaConfigBean JPA配置对象
     * @return 实体管理器工厂Bean
     */
    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(ApplicationContext context,
                                                                           DataSource dataSource,
                                                                           JpaConfigBean jpaConfigBean) {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.valueOf(jpaConfigBean.getJpaDatabase()));
        adapter.setDatabasePlatform(jpaConfigBean.getJpaDatabasePlatform());
        adapter.setGenerateDdl(jpaConfigBean.isGenerateDDL());
        adapter.setShowSql(jpaConfigBean.isShowSQL());
        adapter.setPrepareConnection(true);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan(jpaConfigBean.getJpaEntityPackages());
        emf.setJpaVendorAdapter(adapter);
        return emf;
    }

    /**
     * 创建事务管理器
     *
     * @param containerEntityManagerFactoryBean 实体管理器工厂Bean
     * @return 事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean containerEntityManagerFactoryBean) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(containerEntityManagerFactoryBean.getObject());
        this.transactionManager = transactionManager;
        return transactionManager;
    }

    @Bean(name = "generalAccessorJpa")
    public GeneralAccessor generalAccessorJpa(SessionDataStore sessionDataStore) {
        return new GeneralAccessorImpl(sessionDataStore);
    }

    @Bean(name = "generalDictAccessorJpa")
    public GeneralDictAccessor generalDictAccessorJpa(SessionDataStore sessionDataStore) {
        return new GeneralDictAccessorImpl(sessionDataStore);
    }

    /**
     * 创建一个通用的数据访问器
     *
     * @param context Spring IoC上下文
     * @return 数据访问器
     */
    @Bean(name = "generalAccessor")
    public GeneralAccessor generalAccessor(ApplicationContext context) {
        return context.getBean("generalAccessorJpa", GeneralAccessor.class);
    }

    /**
     * 创建一个通用的字典数据访问器
     *
     * @param context Spring IoC上下文
     * @return 数据访问器
     */
    @Bean(name = "generalDictAccessor")
    public GeneralDictAccessor generalDictAccessor(ApplicationContext context) {
        return context.getBean("generalDictAccessorJpa", GeneralDictAccessor.class);
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
