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
     * @see DataSourceConfigBean#getMinIdle()
     */
    @Override
    public int getMinIdle() {
        return env.getProperty(String.format("%s.minIdle", prefix), Integer.class, 3);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getMaxIdle()
     */
    @Override
    public int getMaxIdle() {
        return env.getProperty(String.format("%s.maxIdle", prefix), Integer.class, 5);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getMaxSize()
     */
    @Override
    public int getMaxSize() {
        return env.getProperty(String.format("%s.maxSize", prefix), Integer.class, 50);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getMaxWaitMillis()
     */
    @Override
    public int getMaxWaitMillis() {
        return env.getProperty(String.format("%s.maxWaitMillis", prefix), Integer.class, 3000);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#isTestOnCreate()
     */
    @Override
    public boolean isTestOnCreate() {
        return env.getProperty(String.format("%s.testOnCreate", prefix), Boolean.class, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#isTestOnBorrow()
     */
    @Override
    public boolean isTestOnBorrow() {
        return env.getProperty(String.format("%s.testOnBorrow", prefix), Boolean.class, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#isTestOnReturn()
     */
    @Override
    public boolean isTestOnReturn() {
        return env.getProperty(String.format("%s.testOnReturn", prefix), Boolean.class, false);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#isTestWhileIdle()
     */
    @Override
    public boolean isTestWhileIdle() {
        return env.getProperty(String.format("%s.testWhileIdle", prefix), Boolean.class, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see DataSourceConfigBean#getValidationQuery()
     */
    @Override
    public String getValidationQuery() {
        return env.getProperty(String.format("%s.validationQuery", prefix), "select 1");
    }
}
