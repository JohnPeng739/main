package com.ds.retl.jdbc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 基于APACHE DBCP2的关系型数据库连接管理器，能够对JDBC数据库连接池进行管理。
 * 数据源配置：
 * [{
 * }]
 * 缓存配置：
 * {
 * "A": {
 * "type": "STATIC",
 * "dataEnum": "a,b,c,d,e,f"
 * },
 * "B": {
 * "type": "JDBC",
 * "dataSource": "dataSource1",
 * "sql": "SELECT code AS B FROM TB_DICT",
 * "intervalSec": 60
 * },
 * "C": {
 * "type": "JDBC",
 * "dataSource": "dataSource1",
 * "sql": "SELECT code AS C FROM TB_DICT_A WHERE AGE &gt; 10",
 * "intervalSec": 3600
 * }
 * }
 *
 * @author : john.peng created on date : 2017/9/12
 */
public class JdbcManager {
    private static final Log logger = LogFactory.getLog(JdbcManager.class);

    private static JdbcManager manager = null;
    private final Serializable loadCacheMutex = "LOAD_CACHE_MUTEX";
    private final int MIN_INTERVAL_SEC = 5;

    private Map<String, BasicDataSource> pool = null;
    private Map<String, JdbcOperate> errorOperates = null;
    private Map<String, List<Object>> caches = null;
    private List<JdbcLoadCacheDefine> loaders = null;
    private Timer loadJdbcTimer = null;

    /**
     * 默认的构造函数
     */
    private JdbcManager() {
        super();
        this.pool = new HashMap<>();
        this.errorOperates = new HashMap<>();
        this.caches = new HashMap<>();
        this.loaders = new ArrayList<>();
    }

    /**
     * 获得管理器的工厂方法
     *
     * @return 管理器
     */
    public static JdbcManager getManager() {
        if (manager == null) {
            manager = new JdbcManager();
        }
        return manager;
    }

    public void initManager(Map<String, Object> conf) throws SQLException {
        String dataSourcesStr = (String) conf.get("jdbcDataSources");
        String cachesStr = (String) conf.get("caches");
        if (!StringUtils.isBlank(dataSourcesStr)) {
            JSONArray dataSourcesJson = JSON.parseArray(dataSourcesStr);
            JSONObject caches = null;
            if (!StringUtils.isBlank(cachesStr)) {
                caches = JSON.parseObject(cachesStr);
            }
            try {
                this.initManager(dataSourcesJson, caches);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Initialize JDBC Manager success, from JdbcSpout, dataSources: %s.", dataSourcesStr));
                }
            } catch (SQLException ex) {
                String message = String.format("Initialize JDBC Manager fail, from JdbcSpout, dataSources: %s.", dataSourcesStr);
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * 初始化管理器
     *
     * @param dataSources 数据源配置
     * @param cacheConfig 缓存配置
     * @throws SQLException 初始化过程中发生的异常
     */
    public void initManager(JSONArray dataSources, JSONObject cacheConfig) throws SQLException {
        this.initDataSources(dataSources);
        this.initCaches(cacheConfig);
    }

    /**
     * 初始化缓存
     *
     * @param cachesConfig 缓存配置
     */
    private void initCaches(JSONObject cachesConfig) {
        if (cachesConfig == null) {
            return;
        }
        cachesConfig.keySet().forEach(key -> {
            String columnName = key;
            JSONObject conf = cachesConfig.getJSONObject(columnName);
            String type = conf.getString("type");
            if ("STATIC".equals(type)) {
                // 静态字典
                String dataEnum = conf.getString("dataEnum");
                loadStaticCache(columnName, dataEnum);
            } else if ("JDBC".equals(type)) {
                // JDBC
                String dataSource = conf.getString("dataSource");
                String sql = conf.getString("sql");
                int intervalSec = conf.getIntValue("intervalSec");
                if (intervalSec <= MIN_INTERVAL_SEC) {
                    intervalSec = MIN_INTERVAL_SEC;
                }
                loaders.add(new JdbcLoadCacheDefine(columnName, dataSource, sql, intervalSec));
            } else {
                throw new IllegalArgumentException(String.format("Unsupported type[%s] for cache type.", type));
            }
        });
        if (loaders != null && loaders.size() > 0) {
            LoadCacheTask cacheTask = new LoadCacheTask();
            cacheTask.loadJdbcCaches();
            loadJdbcTimer = new Timer();
            loadJdbcTimer.scheduleAtFixedRate(cacheTask, MIN_INTERVAL_SEC * 1000, MIN_INTERVAL_SEC * 1000);
        }
    }

    /**
     * 初始化载入一个静态字典数据
     *
     * @param columnName 对应的列名
     * @param fields     逗号分割的字典数据
     */
    private void loadStaticCache(String columnName, String fields) {
        if (StringUtils.isBlank(columnName) || StringUtils.isBlank(fields)) {
            return;
        }
        synchronized (JdbcManager.this.loadCacheMutex) {
            Object[] sides = fields.split(",");
            caches.put(columnName, Arrays.asList(sides));
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Load static cache data, column: %s, enums: %s.", columnName, fields));
            }
        }
    }

    /**
     * 初始化管理器
     *
     * @param dataSourcesJson 初始化配置信息
     * @throws SQLException 初始过程中发生的异常
     */
    private void initDataSources(JSONArray dataSourcesJson) throws SQLException {
        for (int index = 0; index < dataSourcesJson.size(); index++) {
            JSONObject dataSourceJson = dataSourcesJson.getJSONObject(index);
            String name = dataSourceJson.getString("name");
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

    /**
     * 从缓冲池中获取一个数据库连接
     *
     * @param dataSource 数据源名称
     * @return 数据库连接
     * @throws SQLException 获取过程中发生的异常
     */
    public final Connection getConnection(String dataSource) throws SQLException {
        if (!pool.containsKey(dataSource)) {
            throw new SQLException(String.format("The DataSource[%s] not existed.", dataSource));
        }
        BasicDataSource pooledDataSource = pool.get(dataSource);
        return pooledDataSource.getConnection();
    }

    /**
     * 获得统一的错误信息处理工具
     *
     * @param dataSource 数据源名称
     * @return 错误信息处理工具
     * @throws SQLException 获取过程中发生的异常
     */
    public final JdbcOperate getErrorOperate(String dataSource) throws SQLException {
        if (!errorOperates.containsKey(dataSource)) {
            throw new SQLException(String.format("The Error JdbcOperate[%s] not existed.", dataSource));
        }
        return errorOperates.get(dataSource);
    }

    /**
     * 通过查询语句直接实时判定指定的数据是否存在
     *
     * @param columnName 指定的列名
     * @param value      数据值
     * @param dataSource 查询的JDBC数据源
     * @param sql        检测的SQL，其中必须返回列名对应的数据。例如：columnName = "code",
     *                   则可能是：sql = "SELECT CODE AS code FROM TB_TABLE WHERE CODE = ?"
     * @return 如果数据值在指定的返回列中，返回true；否则返回false。
     */
    public final boolean existInReal(String columnName, Object value, String dataSource, String sql) {
        if (StringUtils.isBlank(columnName) || value == null || StringUtils.isBlank(dataSource)
                || StringUtils.isBlank(sql)) {
            return false;
        }
        boolean found = false;
        try {
            Connection conn = this.getConnection(dataSource);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, value);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object tar = rs.getObject(columnName);
                if (value.equals(tar)) {
                    found = true;
                    break;
                }
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Execute query[%s] fail.", sql), ex);
            }
        }
        return found;
    }

    /**
     * 判断指定列的数据是否在缓存中存在，如果输入的列名和数据为null，永远返回不存在。
     *
     * @param columnName 数据列名
     * @param value      数据值
     * @return 如果存在，返回true；否则返回false。
     */
    public final boolean existInCache(String columnName, Object value) {
        if (StringUtils.isBlank(columnName) || value == null) {
            return false;
        }
        if (!caches.containsKey(columnName)) {
            return false;
        }
        List<Object> cache = caches.get(columnName);
        for (Object v : cache) {
            if (value.equals(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从JDBC数据源中加载指定的字典内容
     *
     * @param loader 加载定义
     */
    private void loadJdbcCache(JdbcLoadCacheDefine loader) {
        try {
            Connection conn = this.getConnection(loader.dataSource);
            PreparedStatement ps = conn.prepareStatement(loader.sql);
            ResultSet rs = ps.executeQuery();
            List<Object> list = new ArrayList<>();
            while (rs.next()) {
                Object value = rs.getObject(loader.columnName);
                if (value != null) {
                    list.add(value);
                }
            }
            rs.close();
            ps.close();
            conn.close();
            caches.put(loader.columnName, list);
            loader.lastLoadTime = new Date().getTime();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Auto load cache data from jdbc, column: %s, dataSource: %s, sql: %s, time: %d.",
                        loader.columnName, loader.dataSource, loader.sql, loader.lastLoadTime));
            }
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("load cache fail, sql: %s.", loader.sql), ex);
            }
        }
    }

    /**
     * JDBC数据源缓存加载定义
     */
    private class JdbcLoadCacheDefine {
        private long lastLoadTime = 0;
        private String columnName, dataSource, sql;
        private int interval = 0;

        /**
         * 默认的构造函数
         */
        public JdbcLoadCacheDefine() {
            super();
        }

        /**
         * 构造函数
         *
         * @param columnName  列名
         * @param dataSource  数据源名
         * @param sql         SQL语句
         * @param intervalSec 间隔时间，单位为秒
         */
        public JdbcLoadCacheDefine(String columnName, String dataSource, String sql, int intervalSec) {
            this();
            this.columnName = columnName;
            this.dataSource = dataSource;
            this.sql = sql;
            this.interval = intervalSec * 1000;
        }
    }

    /**
     * JDBC数据源缓存加载任务，最小加载周期为5秒
     */
    private class LoadCacheTask extends TimerTask {
        private long lastLoadTime = 0;

        public void loadJdbcCaches() {
            synchronized (JdbcManager.this.loadCacheMutex) {
                long time = new Date().getTime();
                loaders.forEach(loader -> {
                    if (time - loader.lastLoadTime >= loader.interval) {
                        loadJdbcCache(loader);
                    }
                });
            }
        }

        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            this.loadJdbcCaches();
        }
    }
}
