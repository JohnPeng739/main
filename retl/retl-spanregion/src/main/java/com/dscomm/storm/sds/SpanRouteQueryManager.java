/**
 *
 */
package com.dscomm.storm.sds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author john
 */
public class SpanRouteQueryManager {
    private static final Log logger = LogFactory
            .getLog(SpanRouteQueryManager.class);
    private static SpanRouteQueryManager manager = null;

    private String driverClass = "oracle.jdbc.driver.OracleDriver";
    private String dbUrl = "jdbc:oracle:thin:@localhost:1521/DSDB";
    private String dbUser = "ds110";
    private String dbPassword = "edmund";
    private String dbQueryRoute = "SELECT REGION_CODE, JMS_URL, JMS_USER, JMS_PWD, JMS_QUEUE FROM T_SPANREGION "
            + "WHERE TYPE = ?";
    private long period = 30;

    private boolean needExit = false;

    private Connection dbConnection = null;

    private Map<String, Map<String, MessageDistinationInfor>> routes = new HashMap<String, Map<String, MessageDistinationInfor>>(
            20);

    /**
     * 根据传入的参数获得数据查询实例
     *
     * @param driverClass  数据库驱动类
     * @param dbUrl        数据库连接字符串
     * @param dbUser       连接用户
     * @param dbPassword   用户密码
     * @param dbQueryRoute 路由查询语句<br>
     *                     SELECT中的顺序必须是：区域代码、JMS的URL，JMS用户名，JMS用户密码，JMS队列
     * @param period       从数据库加载路由信息表的时间周期，单位为秒。
     * @return
     */
    public static SpanRouteQueryManager instance(String driverClass,
                                                 String dbUrl, String dbUser, String dbPassword,
                                                 String dbQueryRoute, long period) {
        if (manager == null) {
            manager = new SpanRouteQueryManager();
        }
        manager.driverClass = driverClass;
        manager.dbUrl = dbUrl;
        manager.dbUser = dbUser;
        manager.dbPassword = dbPassword;
        manager.dbQueryRoute = dbQueryRoute;
        manager.period = period;
        // 初始连接数据库
        manager.initialize();
        return manager;
    }

    /**
     * 默认的构造函数
     */
    private SpanRouteQueryManager() {
        super();
    }

    public void close() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Close the db connection fail.", ex);
                }
            }
            dbConnection = null;
        }
        // 发出关闭同步线程的信号
        needExit = true;
        // 等待5秒
        try {
            Thread.sleep(5000);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("sleep fail.", ex);
            }
        }
    }

    // 从数据库获取路由信息，并根据行政区划码进行匹配
    public List<MessageDistinationInfor> fetchMessageDistinations(
            String spanType, String to, String cc) throws Exception {
        List<MessageDistinationInfor> list = new Vector<MessageDistinationInfor>();

        // 根据目标行政区划码进行路由
        // 行政区划码：省厅、地市为4位，区县为6位。
        // 获取直接发送的路由信息
        String[] codes = to.split(",");
        for (String code : codes) {
            // 首先全部匹配（6位）
            if (logger.isInfoEnabled()) {
                logger.info("The region in is:[" + code + "] ");
            }
            MessageDistinationInfor infor = findDestinationRoute(spanType, code);
            if (logger.isInfoEnabled()) {
                logger.info("The region out is:[" + infor.getRegionCode() + "] ");
            }
            if (infor == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The region [" + code
                            + "] can not found a validated route.");
                }
                continue;
            }
            // 这里不需要设置jmsType，默认就是DIRECT。
            list.add(infor);
        }
        // @20140805 修正无cc的bug
        if (cc == null || "".equals(cc))
            return list;
        // 获取抄送的路由信息
        codes = cc.split(",");
        for (String code : codes) {
            MessageDistinationInfor infor = findDestinationRoute(spanType, code);
            if (infor == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The region [" + code
                            + "] can not found a validated route.");
                }
                continue;
            }
            // 将jmsType设置为REPORT类型。
            infor.setJmsType(MessageDistinationInfor.TYPE_REPORT);
            list.add(infor);
        }
        return list;
    }

    // 为指定的行政区划查找合适的派发路由
    private MessageDistinationInfor findDestinationRoute(String spanType,
                                                         String code) {
        String destRegionCode = code;
        synchronized (routes) {
            Map<String, MessageDistinationInfor> map = routes.get(spanType);
            if (map == null || map.size() <= 0) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The routes' map is empty.");
                }
                return null;
            }
            code = code.trim();
            MessageDistinationInfor infor = map.get(code);
            while (infor == null && code.length() > 0) {
                code = code.substring(0, code.length() - 1);
                infor = map.get(code);
            }
            //从缓存里拿出数据必须克隆，否则新的数据可能被不断刷新的缓存覆盖
            MessageDistinationInfor optInfor = infor.clone();
            optInfor.setRegionCode(destRegionCode);
            return optInfor;
        }
    }

    private void synchronizeRoutes() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = dbConnection.prepareStatement(dbQueryRoute);
            rs = ps.executeQuery();
            String regionCode, jmsUrl, jmsUser, jmsPwd, jmsQueue, type;
            synchronized (routes) {
                while (rs.next()) {
                    // 1 - REGION_CODE
                    regionCode = rs.getString(1);
                    // 2 - JMS_URL
                    jmsUrl = rs.getString(2);
                    // 3 - JMS_USER
                    jmsUser = rs.getString(3);
                    // 4 - JMS_PWD
                    jmsPwd = rs.getString(4);
                    // 5 - JMS_QUEUE
                    jmsQueue = rs.getString(5);
                    // 6 - TYPE
                    type = rs.getString(6);

                    Map<String, MessageDistinationInfor> map = null;
                    if (routes.containsKey(type)) {
                        map = routes.get(type);
                    } else {
                        map = new HashMap<String, MessageDistinationInfor>();
                        routes.put(type, map);
                    }
                    MessageDistinationInfor infor = null;
                    if (map.containsKey(regionCode)) {
                        infor = map.get(regionCode);
                    } else {
                        infor = new MessageDistinationInfor();
                        map.put(regionCode, infor);
                    }
                    infor.setJmsUrl(jmsUrl);
                    infor.setJmsUser(jmsUser);
                    infor.setJmsPassowrd(jmsPwd);
                    infor.setJmsQueue(jmsQueue);
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("Synchronize route's information success.");
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("query [" + dbQueryRoute + "] faile", ex);
            }
            try {
                // 延时30秒再进行连接
                Thread.sleep(30 * 1000);
            } catch (Exception ex1) {
                if (logger.isErrorEnabled()) {
                    logger.error("sleep fail.", ex1);
                }
            }
            // 进行数据库重连
            initialize();
        } finally {// @20140805 将关闭资源方法移入finally中
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("rs close error", e);
                }
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("ps close error", e);
                }
        }
    }

    // 初始化并连接数据库连接
    private void initialize() {
        try {
            // 先初始化并连接数据库
            Class.forName(driverClass);
            dbConnection = DriverManager.getConnection(dbUrl, dbUser,
                    dbPassword);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Initialize db connection fail.", ex);
            }
        }
        try {
            // 然后启动同步更新路由信息
            synchronizeRoutes();
            new Thread() {
                public void run() {
                    while (!needExit) {
                        // 从数据库同步路由信息
                        // 同步成功后延时
                        try {
                            Thread.sleep(period * 1000);
                        } catch (Exception ex) {
                            if (logger.isErrorEnabled()) {
                                logger.error("sleep fail.", ex);
                            }
                        }
                        synchronizeRoutes();
                    }
                }
            }.start();
            // 主线程先等待3秒，待路由信息第一次同步。
            // Thread.sleep(3000);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("start sychronize routes thread fail.", ex);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("initialize success.");
        }

    }

}
