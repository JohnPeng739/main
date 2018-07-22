package org.mx.dbcp;

import org.mx.dal.config.DataSourceConfigBean;
import org.springframework.core.env.Environment;

/**
 * 描述： DBCP2数据源配置对象
 *
 * @author john peng
 * Date time 2018/7/22 下午12:21
 */
public class Dbcp2DataSourceConfigBean implements DataSourceConfigBean {
    private Environment env;
    private String prefix;

    /**
     * 构造函数
     *
     * @param env    Spring IoC上下文环境
     * @param prefix 配置前缀
     */
    public Dbcp2DataSourceConfigBean(Environment env, String prefix) {
        super();
        this.env = env;
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getDriver()
     */
    @Override
    public String getDriver() {
        return env.getProperty(String.format("%s.driver", prefix));
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getUrl()
     */
    @Override
    public String getUrl() {
        return env.getProperty(String.format("%s.url", prefix));
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getUser()
     */
    @Override
    public String getUser() {
        return env.getProperty(String.format("%s.user", prefix));
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getPassword()
     */
    @Override
    public String getPassword() {
        return env.getProperty(String.format("%s.password", prefix));
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getInitialSize()
     */
    @Override
    public int getInitialSize() {
        return env.getProperty(String.format("%s.initialSize", prefix), Integer.class, 1);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getMaxSize()
     */
    @Override
    public int getMaxSize() {
        return env.getProperty(String.format("%s.maxSize", prefix), Integer.class, 100);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getMaxIdleTime()
     */
    @Override
    public int getMaxIdleTime() {
        return env.getProperty(String.format("%s.maxIdleTime", prefix), Integer.class, 3000);
    }
}
