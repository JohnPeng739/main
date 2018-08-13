package org.mx.dbcp;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.config.DataSourceConfigBean;
import org.mx.error.UserInterfaceSystemErrorException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * APACHE DBCP2数据库缓存池工厂
 *
 * @author : john.peng date : 2017/10/7
 */
public class Dbcp2DataSourceFactory {
    private static final Log logger = LogFactory.getLog(Dbcp2DataSourceFactory.class);

    private BasicDataSource pool = null;
    private DataSourceConfigBean dataSourceConfigBean;

    /**
     * 构造函数
     *
     * @param dataSourceConfigBean 数据源配置对象
     */
    public Dbcp2DataSourceFactory(DataSourceConfigBean dataSourceConfigBean) {
        super();
        this.dataSourceConfigBean = dataSourceConfigBean;
    }

    /**
     * 获取当前有效的连接数
     *
     * @return 有效连接数
     */
    public int getNumActive() {
        return pool.getNumActive();
    }

    /**
     * 获取当前空闲连接数
     *
     * @return 空闲连接数
     */
    public int getNumIdle() {
        return pool.getNumIdle();
    }

    /**
     * 从缓冲池中获取一个数据源
     *
     * @return 数据库连接
     */
    public DataSource getDataSource() {
        return pool;
    }

    /**
     * 从缓冲池中获取一个数据库连接
     *
     * @return 数据库连接
     * @throws SQLException 获取过程中发生的异常
     */
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    /**
     * 初始化缓冲池
     *
     * @throws SQLException 初始化过程中发生的异常
     */
    public void init() throws SQLException {
        String driver = dataSourceConfigBean.getDriver(),
                url = dataSourceConfigBean.getUrl(),
                user = dataSourceConfigBean.getUser(),
                password = dataSourceConfigBean.getPassword();
        if (StringUtils.isBlank(driver) || StringUtils.isBlank(url)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Data source config invalid, driver: %s, url: %s, user: %s, password: %s.",
                        driver, url, user, password));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        try {
            pool = new BasicDataSource();
            pool.setDriverClassName(driver);
            pool.setUrl(url);
            pool.setUsername(user);
            pool.setPassword(password);
            pool.setInitialSize(dataSourceConfigBean.getInitialSize());
            pool.setMinIdle(dataSourceConfigBean.getMinIdle());
            pool.setMaxIdle(dataSourceConfigBean.getMaxIdle());
            pool.setMaxTotal(dataSourceConfigBean.getMaxSize());
            pool.setMaxWaitMillis(dataSourceConfigBean.getMaxWaitMillis());
            pool.setTestOnCreate(dataSourceConfigBean.isTestOnCreate());
            pool.setTestOnBorrow(dataSourceConfigBean.isTestOnBorrow());
            pool.setTestOnReturn(dataSourceConfigBean.isTestOnReturn());
            pool.setTestWhileIdle(dataSourceConfigBean.isTestWhileIdle());
            pool.setValidationQuery(dataSourceConfigBean.getValidationQuery());
        } catch (Exception ex) {
            String msg = "Init DBCP2 pool fail.";
            if (logger.isErrorEnabled()) {
                logger.error(msg, ex);
            }
            throw new SQLException(msg, ex);
        }
    }

    /**
     * 关闭缓冲池
     *
     * @throws SQLException 关闭过程中发生的异常
     */
    public void close() throws SQLException {
        try {
            if (pool != null) {
                pool.close();
                if (logger.isDebugEnabled()) {
                    logger.debug("Close DBCP2 pool successfully.");
                }
            }
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Close DBCP2 pool fail.", ex);
            }
            throw ex;
        }
    }
}
