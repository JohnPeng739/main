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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author john
 */
public class DispatchSpanRegionCommandBasicBolt extends BaseBasicBolt {
    private static final long serialVersionUID = 5888701448470753263L;
    private static final Log logger = LogFactory.getLog(DispatchSpanRegionCommandBasicBolt.class);

    private String driverClass = "oracle.jdbc.driver.OracleDriver";
    private String dbUrl = "jdbc:oracle:thin:@localhost:1521/DSDB";
    private String dbUser = "ds110";
    private String dbPassword = "edmund";
    private String dbQueryRoute = "SELECT REGION_CODE, JMS_URL, JMS_USER, JMS_PWD, JMS_QUEUE FROM T_SPANREGION "
            + "WHERE TYPE = ?";
    private long period = 30; // 30秒重新同步数据库路由信息

    private SpanRouteQueryManager manager = null;

    /**
     * 默认的构造函数
     */
    public DispatchSpanRegionCommandBasicBolt() {
        super();
    }

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
            List<MessageDistinationInfor> routes = manager.fetchMessageDistinations(type, to, cc);

            for (MessageDistinationInfor infor : routes) {
                dispatchMessage(infor.getJmsUrl(), infor.getJmsUser(), infor.getJmsPassowrd(), infor.getJmsQueue(),
                        "direct", data, time1);
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Dispatch the span region command fail.", ex);
            }
            throw new RuntimeException(ex);
        }
    }

    // 进行消息派发
    private void dispatchMessage(String jmsUrl, String jmsUser, String jmsPassword, String jmsQueue, String type,
                                 String data, long time) throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory(jmsUser, jmsPassword, jmsUrl);
        javax.jms.Connection jmsConnection = factory.createConnection();
        jmsConnection.start();
        Session jmsSession = jmsConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
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

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseBasicBolt#prepare(java.util.Map,
     * backtype.storm.task.TopologyContext)
     */
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map config, TopologyContext context) {
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

        super.prepare(config, context);
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseBasicBolt#cleanup()
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
     * @see
     * backtype.storm.topology.IComponent#declareOutputFields(backtype.storm
     * .topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Bolt直接将处理后的数据发送到MQ中，不需要再次发射消息。
    }

}
