package com.ds.retl.jdbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 2017/9/12.
 */
public class JdbcManager {
    private static final Log logger = LogFactory.getLog(JdbcManager.class);

    private static JdbcManager manager = null;

    private Map<String, BasicDataSource> pool = null;
    private Map<String, JdbcOperate> errorOperates = null;
    private JdbcManager() {
        super();
        this.pool = new HashMap<>();
        this.errorOperates = new HashMap<>();
    }

    public static JdbcManager getManager() {
        if (manager == null) {
            manager = new JdbcManager();
        }
        return manager;
    }

    public void initManager(JSONArray dataSourcesJson) throws SQLException {
        for (int index = 0; index < dataSourcesJson.size(); index++) {
            JSONObject dataSourceJson = dataSourcesJson.getJSONObject(index);
            String name = dataSourceJson.getString("dataSource");
            if (pool.containsKey(name)) {
                continue;
            }
            BasicDataSource dataSource = new BasicDataSource();
            String driver = dataSourceJson.getString("driver"),
                    url = dataSourceJson.getString("url"),
                    user = dataSourceJson.getString("user"),
                    password = dataSourceJson.getString("password");
            if (StringUtils.isBlank(driver)) {
                throw new SQLException("The JDBC driver is blank.");
            }
            if (StringUtils.isBlank(url)) {
                throw new SQLException("The JDBC url is blank.");
            }
            int initialPoolSize = dataSourceJson.getIntValue("initialPoolSize"),
                    maxPoolSize = dataSourceJson.getIntValue("maxPoolSize"),
                    maxIdleTime = dataSourceJson.getIntValue("maxIdleTIme");
            try {
                dataSource.setDriverClassName(driver);
                dataSource.setUrl(url);
                dataSource.setUsername(user);
                dataSource.setPassword(password);
                dataSource.setInitialSize(initialPoolSize);
                dataSource.setMaxTotal(maxPoolSize);
                dataSource.setMaxIdle(maxIdleTime);
            } catch (Exception ex) {
                throw new SQLException(ex.getMessage(), ex);
            }
            pool.put(name, dataSource);

            JSONObject errorTableJson = dataSourceJson.getJSONObject("errorTable");
            if (errorTableJson != null) {
                JdbcOperate errorOperate = new JdbcOperate(dataSource.getConnection(), errorTableJson);
                this.errorOperates.put(name, errorOperate);
            }
        }
    }

    public final Connection getConnection(String dataSource) throws SQLException {
        if (!pool.containsKey(dataSource)) {
            throw new SQLException(String.format("The DataSource[%s] not existed.", dataSource));
        }
        BasicDataSource pooledDataSource = pool.get(dataSource);
        return pooledDataSource.getConnection();
    }

    public final JdbcOperate getErrorOperate(String dataSource) throws SQLException {
        if (!errorOperates.containsKey(dataSource)) {
            throw new SQLException(String.format("The Error JdbcOperate[%s] not existed.", dataSource));
        }
        return errorOperates.get(dataSource);
    }
}
