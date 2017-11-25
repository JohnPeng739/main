/**
 *
 */
package com.dscomm.storm.sds;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author john
 */
public class ReceiveSpanRegionCommandSpout extends BaseRichSpout {
    private static final long serialVersionUID = -5139962214728987167L;
    private static final Log logger = LogFactory
            .getLog(ReceiveSpanRegionCommandSpout.class);

    private String jmxUrl = "tcp://192.168.2.3:61616", jmxUser = "ds110",
            jmxPassword = "edmund", jmxQueue = "sdst.span-region-commander";
    private String toID = "span.to", ccID = "span.cc", typeID = "span.type";
    private long retryNum = 30;

    private SpoutOutputCollector _collector = null;
    private Connection jmsConnection = null;
    private Session jssSession = null;
    private MessageConsumer jmsConsumer = null;

    private Map<Object, Message> messages = new HashMap<Object, Message>();

    /**
     * 默认的构造函数
     */
    public ReceiveSpanRegionCommandSpout() {
        super();
    }

    // 初始化并连接JMS服务器
    private void initJmxConnection() {
        ConnectionFactory factory = new ActiveMQConnectionFactory(jmxUser,
                jmxPassword, jmxUrl);
        try {
            jmsConnection = factory.createConnection();
            jmsConnection.start();
            jssSession = jmsConnection.createSession(true,
                    Session.CLIENT_ACKNOWLEDGE);
            if (jmsConnection instanceof ActiveMQConnection) {
                // 设置重试次数
                RedeliveryPolicy policy = ((ActiveMQConnection) jmsConnection)
                        .getRedeliveryPolicy();
                policy.setMaximumRedeliveries((int) retryNum);
                ((ActiveMQConnection) jmsConnection)
                        .setRedeliveryPolicy(policy);
            }
            Destination dest = jssSession.createQueue(jmxQueue);
            jmsConsumer = jssSession.createConsumer(dest);
            if (logger.isInfoEnabled()) {
                logger.info("init message client success.");
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("init message client fail.", ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.spout.ISpout#open(java.util.Map,
     * backtype.storm.task.TopologyContext,
     * backtype.storm.spout.SpoutOutputCollector)
     */
    @Override
    public void open(@SuppressWarnings("rawtypes") Map config,
                     TopologyContext context, SpoutOutputCollector collector) {
        this._collector = collector;
        String tmpStr = (String) config.get("mq.url");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            jmxUrl = tmpStr;
        }
        tmpStr = (String) config.get("mq.user");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            jmxUser = tmpStr;
        }
        tmpStr = (String) config.get("mq.password");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            jmxPassword = tmpStr;
        }
        tmpStr = (String) config.get("mq.queue.request");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            jmxQueue = tmpStr;
        }
        tmpStr = (String) config.get("span.to.id");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            toID = tmpStr;
        }
        tmpStr = (String) config.get("span.cc.id");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            ccID = tmpStr;
        }
        tmpStr = (String) config.get("span.type.id");
        if (tmpStr != null && tmpStr.trim().length() > 0) {
            typeID = tmpStr;
        }
        Object number = config.get("retry.number");
        if (number != null) {
            retryNum = (Long) number;
        }

        initJmxConnection();
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseRichSpout#close()
     */
    @Override
    public void close() {
        if (jmsConnection != null) {
            try {
                jmsConsumer.close();
                jssSession.close();
                jmsConnection.close();
                jmsConsumer = null;
                jssSession = null;
                jmsConnection = null;
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("close the message client fail.", ex);
                }
            }
        }
        super.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.spout.ISpout#nextTuple()
     */
    @Override
    public void nextTuple() {
        try {
            while (true) {
                TextMessage message = (TextMessage) jmsConsumer.receive(50);
                if (message != null) {
                    String msgId = message.getJMSMessageID();
                    String type = message.getStringProperty(typeID);
                    String to = message.getStringProperty(toID);
                    String cc = message.getStringProperty(ccID);
                    if (logger.isInfoEnabled()) {
                        logger.info("Fetch a Span Region Command message["
                                + msgId + "], to: [" + to + "] cc: [" + cc
                                + "], emit it!");
                    }
                    Values values = new Values(type, to, cc, message.getText(),
                            new Date().getTime());
                    this._collector.emit(values, msgId);
                    if (logger.isInfoEnabled()) {
                        logger.info("Emit a message: [" + message.getText()
                                + "].");
                    }
                    messages.put(msgId, message);
                } else {
                    break;
                }
            }
            Thread.sleep(100);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Receive span region command message fail.", ex);
            }
            // 重新连接JMS服务器
            initJmxConnection();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseRichSpout#ack(java.lang.Object)
     */
    @Override
    public void ack(Object msgId) {
        if (logger.isInfoEnabled()) {
            logger.info("**********acknowledge the jms message.*****************");
        }
        Message message = messages.get(msgId);
        if (message != null) {
            try {
                message.acknowledge();
                jssSession.commit();
                if (logger.isInfoEnabled()) {
                    logger.info("ACK sig, Commit JMS.");
                }
                messages.remove(msgId);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Commit JMS message fail.", ex);
                }
            }
        }
        super.ack(msgId);
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.topology.base.BaseRichSpout#fail(java.lang.Object)
     */
    @Override
    public void fail(Object msgId) {
        Message message = messages.get(msgId);
        if (message != null) {
            try {
                jssSession.rollback();
                if (logger.isInfoEnabled()) {
                    logger.info("FAIL sig, Rollback JMS.");
                }
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Commit JMS message fail.", ex);
                }
            }
        }
        super.fail(msgId);
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
        declarer.declare(new Fields("type", "to", "cc", "data", "time"));
    }

}
