package org.mx.dal.config;

import org.mx.dal.util.Dbcp2DataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * DAL（数据访问层）Java Configure类
 * <p>
 * 扫描包：org.mx.dal.session.impl中的组件
 * 加载配置文件：classpath:database.properties
 *
 * @author : john.peng date : 2017/10/7
 */
@Configuration
@PropertySource({"classpath:database.properties"})
@ComponentScan({
        "org.mx.dal.session.impl"
})
public class DalConfig {
    @Autowired
    private Environment env = null;

    /**
     * 默认的构造函数
     */
    public DalConfig() {
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
}
