/**
 *
 */
package com.dscomm.storm.sds;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import javax.jms.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * @author john
 */
public class DispatchSpanRegionCommandBolt extends BaseBasicBolt {
    private static final long serialVersionUID = 5888701448470753263L;
    private static final Log logger = LogFactory
            .getLog(DispatchSpanRegionCommandBolt.class);

    private String driverClass = "oracle.jdbc.driver.OracleDriver";
    private String dbUrl = "jdbc:oracle:thin:@localhost:1521/DSDB";
    private String dbUser = "ds110";
    private String dbPassword = "edmund";

    private Connection dbConnection = null;

    /**
     * 默认的构造函数
     */
    public DispatchSpanRegionCommandBolt() {
        super();
    }

    //

    /*
     * (non-Javadoc)
     *
     * @see
     * backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple,
     * backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String type = input.getStringByField("type");
        String to = input.getStringByField("to");
        String cc = input.getStringByField("cc");
        String data = input.getStringByField("data");
        long time1 = input.getLongByField("time");

        try {
            List<MessageDistinationInfor> routes = fetchMessageDistinations(
                    type, to, cc);

            for (MessageDistinationInfor infor : routes) {
                dispatchMessage(infor.getJmsUrl(), infor.getJmsUser(),
                        infor.getJmsPassowrd(), infor.getJmsQueue(), "direct",
                        data, time1);
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Dispatch the span region command fail.", ex);
            }
        }
    }

    // 从数据库获取路由信息，并根据行政区划码进行匹配
    private List<MessageDistinationInfor> fetchMessageDistinations(
            String spanType, String to, String cc) throws Exception {
        List<MessageDistinationInfor> list = new Vector<MessageDistinationInfor>();

        // 根据目标代码从数据库查询目标地址等信息。
        List<String[]> dataRows = new Vector<String[]>();
        String sql = "SELECT REGION_CODE, JMS_URL, JMS_USER, JMS_PWD, JMS_QUEUE "
                + "FROM T_SPANREGION " + "WHERE TYPE = ?";
        try {
            PreparedStatement ps = dbConnection.prepareStatement(sql);
            ps.setString(1, spanType);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] row = new String[5];
                row[0] = rs.getString("REGION_CODE");
                row[1] = rs.getString("JMS_URL");
                row[2] = rs.getString("JMS_USER");
                row[3] = rs.getString("JMS_PWD");
                row[4] = rs.getString("JMS_QUEUE");
                dataRows.add(row);
            }
            rs.close();
            ps.close();

            // 根据目标行政区划码进行路由
            // 行政区划码：省厅、地市为4位，区县为6位。
            // 获取直接发送的路由信息
            String[] codes = to.split(",");
            for (String code : codes) {
                // 首先全部匹配（6位）
                MessageDistinationInfor infor = findDestinationRoute(dataRows,
                        code, 6);
                if (infor == null) {
                    // 全匹配没有找到，找地市
                    infor = findDestinationRoute(dataRows, code, 4);
                    if (infor == null) {
                        // 地市匹配没有找到，找省厅
                        infor = findDestinationRoute(dataRows, code, 2);
                    }
                }
                if (infor == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("The region [" + code
                                + "] can not found a validated route.");
                    }
                    continue;
                }
                list.add(infor);
            }
            // 获取抄送的路由信息
            codes = cc.split(",");
            for (String code : codes) {
                MessageDistinationInfor infor = findDestinationRoute(dataRows,
                        code, 6);
                if (infor == null) {
                    // 全匹配没有找到，找地市
                    infor = findDestinationRoute(dataRows, code, 4);
                    if (infor == null) {
                        // 地市匹配没有找到，找省厅
                        infor = findDestinationRoute(dataRows, code, 2);
                    }
                }
                if (infor == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("The region [" + code
                                + "] can not found a validated route.");
                    }
                    continue;
                }
                list.add(infor);
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("query [" + sql + "] faile", ex);
            }
            // 进行数据库重连
            initDbConnection();
            throw ex;
        }
        return list;
    }

    // 为指定的行政区划查找合适的派发路由
    private MessageDistinationInfor findDestinationRoute(
            List<String[]> dataRows, String code, int length) {
        // dataRow的数据顺序： REGION_CODE, URL, USER, PASSWORD, QUEUE
        code = code.trim();
        length = Math.min(length, code.length());
        for (String[] row : dataRows) {
            // 全部匹配或者前面部分匹配
            if (row[0].equals(code.substring(0, length))) {
                // found
                MessageDistinationInfor infor = new MessageDistinationInfor();
                infor.setJmsUrl(row[1]);
                infor.setJmsUser(row[2]);
                infor.setJmsPassowrd(row[3]);
                infor.setJmsQueue(row[4]);
                return infor;
            }
        }
        // not found, return null.
        return null;
    }

    // 进行消息派发
    private void dispatchMessage(String jmsUrl, String jmsUser,
                                 String jmsPassword, String jmsQueue, String type, String data,
                                 long time) throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory(jmsUser,
                jmsPassword, jmsUrl);
        javax.jms.Connection jmsConnection = factory.createConnection();
        jmsConnection.start();
        Session jmsSession = jmsConnection.createSession(false,
                Session.CLIENT_ACKNOWLEDGE);
        Destination dest = jmsSession.createQueue(jmsQueue);
        MessageProducer jmsProducer = jmsSession.createProducer(dest);
        if (logger.isInfoEnabled()) {
            logger.info("init message client success.");
        }
        TextMessage message = jmsSession.createTextMessage(data);
        message.setStringProperty("command.type", type);
        message.setLongProperty("time1", time);
        message.setLongProperty("time2", new Date().getTime());
        jmsProducer.send(message);
        jmsProducer.close();
        jmsSession.close();
        jmsConnection.close();
    }

    // 初始化并连接数据库连接
    private void initDbConnection() {
        try {
            Class.forName(driverClass);
            dbConnection = DriverManager.getConnection(dbUrl, dbUser,
                    dbPassword);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Initialize db connection fail.", ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseBasicBolt#prepare(java.util.Map,
     * backtype.storm.task.TopologyContext)
     */
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map config,
                        TopologyContext context) {
        String tmpStr = (String) config.get("db.driver");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            driverClass = tmpStr;
        }
        tmpStr = (String) config.get("db.url");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            dbUrl = tmpStr;
        }
        tmpStr = (String) config.get("db.user");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            dbUser = tmpStr;
        }
        tmpStr = (String) config.get("db.password");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            dbPassword = tmpStr;
        }

        initDbConnection();

        super.prepare(config, context);
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseBasicBolt#cleanup()
     */
    @Override
    public void cleanup() {
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
        super.cleanup();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * backtype.storm.topology.IComponent#declareOutputFields(backtype.storm
     * .topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Bolt直接将处理后的数据发送到MQ中，不需要再次发射消息。
    }

}
