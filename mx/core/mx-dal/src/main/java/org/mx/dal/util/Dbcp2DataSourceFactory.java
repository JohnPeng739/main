package org.mx.dal.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * APACHE DBCP2数据库缓存池工厂
 *
 * @author : john.peng date : 2017/10/7
 */
public class Dbcp2DataSourceFactory {
    private static final Log logger = LogFactory.getLog(Dbcp2DataSourceFactory.class);

    private BasicDataSource pool = null;
    private Environment env;

    /**
     * 默认的构造函数
     *
     * @param env 配置信息环境
     */
    public Dbcp2DataSourceFactory(Environment env) {
        super();
        this.env = env;
    }

    /**
     * 从缓冲池中获取一个数据库连接
     *
     * @return 数据库连接
     */
    public DataSource getDataSource() {
        return pool;
    }

    /**
     * 初始化缓冲池
     *
     * @throws SQLException 初始化过程中发生的异常
     */
    public void init() throws SQLException {
        String driver = env.getProperty("db.driver"),
                url = env.getProperty("db.url"),
                user = env.getProperty("db.user"),
                password = env.getProperty("db.password");
        int initialSize = env.getProperty("db.initialSize", Integer.class, 3),
                maxSize = env.getProperty("db.maxSize", Integer.class, 50),
                maxIdleTime = env.getProperty("db.maxIdleTime", Integer.class, 3000);
        try {
            pool = new BasicDataSource();
            pool.setDriverClassName(driver);
            pool.setUrl(url);
            pool.setUsername(user);
            pool.setPassword(password);
            pool.setInitialSize(initialSize);
            pool.setMaxTotal(maxSize);
            pool.setMaxIdle(maxIdleTime);
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
