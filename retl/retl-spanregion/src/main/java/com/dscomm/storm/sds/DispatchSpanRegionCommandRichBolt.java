/**
 *
 */
package com.dscomm.storm.sds;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author john
 */
public class DispatchSpanRegionCommandRichBolt extends BaseRichBolt {
    private static final long serialVersionUID = -3367366983571647585L;
    private static final Log logger = LogFactory.getLog(DispatchSpanRegionCommandRichBolt.class);
    private OutputCollector _collector = null;

    private String driverClass = "oracle.jdbc.driver.OracleDriver";
    private String dbUrl = "jdbc:oracle:thin:@localhost:1521/DSDB";
    private String dbUser = "ds110";
    private String dbPassword = "edmund";
    private String dbQueryRoute = "SELECT REGION_CODE, JMS_URL, JMS_USER, JMS_PWD, JMS_QUEUE FROM T_SPANREGION "
            + "WHERE TYPE = ?";
    private long period = 30; // 30秒重新同步数据库路由信息

    private SpanRouteQueryManager manager = null;
    private final Vector<Session> sessions = new Vector<Session>();
    private final Vector<javax.jms.Connection> connections = new Vector<javax.jms.Connection>();

    /**
     * 默认的构造函数
     */
    public DispatchSpanRegionCommandRichBolt() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.task.IBolt#prepare(java.util.Map,
     * backtype.storm.task.TopologyContext, backtype.storm.task.OutputCollector)
     */
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map config, TopologyContext context, OutputCollector collector) {
        this._collector = collector;

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
        tmpStr = (String) config.get("db.query.route");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            dbQueryRoute = tmpStr;
        }
        Object tmpPeriod = config.get("db.query.period");
        if (tmpPeriod != null) {
            period = (Long) tmpPeriod;
        }

        manager = SpanRouteQueryManager.instance(driverClass, dbUrl, dbUser, dbPassword, dbQueryRoute, period);
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseRichBolt#cleanup()
     */
    @Override
    public void cleanup() {
        if (manager != null) {
            manager.close();
        }
        super.cleanup();
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
     */
    @Override
    public void execute(Tuple input) {
        String type = input.getStringByField("type");
        String to = input.getStringByField("to");
        String cc = input.getStringByField("cc");
        String data = input.getStringByField("data");
        long time1 = input.getLongByField("time");
        if (logger.isInfoEnabled()) {
            logger.info("type field value is: [" + type + "].");
        }
        if (logger.isInfoEnabled()) {
            logger.info("to field value is: [" + to + "].");
        }
        if (logger.isInfoEnabled()) {
            logger.info("cc field value is: [" + cc + "].");
        }

        try {
            List<MessageDistinationInfor> routes = manager.fetchMessageDistinations(type, to, cc);

            for (MessageDistinationInfor infor : routes) {
                dispatchMessage(infor, data, time1, type);
            }
            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                javax.jms.Connection conn = connections.get(i);
                session.commit();
                session.close();
                conn.close();
            }
            if (logger.isInfoEnabled()) {
                logger.info("dispatch message success, message: [" + data + "].");
            }
            _collector.ack(input);
            System.out.println("11");
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Dispatch the span region command fail.", ex);
            }
            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                javax.jms.Connection conn = connections.get(i);
                try {
                    session.rollback();
                    session.close();
                    conn.close();
                } catch (Exception ex1) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Rollback fail.", ex);
                    }
                }
            }
            _collector.fail(input);
        }
        sessions.clear();
        connections.clear();
    }

    // 进行消息派发
    private void dispatchMessage(MessageDistinationInfor infor, String data, long time, String type) throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory(infor.getJmsUser(), infor.getJmsPassowrd(),
                infor.getJmsUrl());
        javax.jms.Connection jmsConnection = factory.createConnection();
        jmsConnection.start();
        Session jmsSession = jmsConnection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
        sessions.add(jmsSession);
        connections.add(jmsConnection);
        Destination dest = jmsSession.createQueue(infor.getJmsQueue());
        MessageProducer jmsProducer = jmsSession.createProducer(dest);
        if (logger.isInfoEnabled()) {
            logger.info("init message client success.");
        }
        TextMessage message = jmsSession.createTextMessage(data);
        // 设置本消息的处理类型：DIRECT or REPORT
        message.setStringProperty("span.type", infor.getJmsType());
        // 设置本消息开始处理的时间
        message.setLongProperty("time1", time);
        // 设置本消息处理结束的时间
        message.setLongProperty("time2", new Date().getTime());
        // 设置本消息最终处理的目标区域代码。
        if (logger.isInfoEnabled()) {
            logger.info("the span.dest value to set is: [" + infor.getRegionCode() + "].");
        }
        message.setStringProperty("span.dest", infor.getRegionCode());
        // @bezaleel 因.net服务对于点号无法进行过滤，改为dest和type
        message.setStringProperty("dest", infor.getRegionCode());
        message.setStringProperty("type", type);
        // @bezaleel end
        jmsProducer.send(message);
        jmsProducer.close();
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
