package com.ds.retl.spout;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.Config;
import org.apache.storm.jms.JmsProvider;
import org.apache.storm.jms.JmsTupleProducer;
import org.apache.storm.jms.spout.JmsMessageID;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import javax.jms.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 拉取型JMS数据采集器，默认的推拉型JMS数据采集器没有使用数据流量限制，在大消息量堆积情况下可能存在风险，推荐使用拉取型JMS数据采集器。
 *
 * @author : john.peng created on date : 2017/9/26
 */
public class JmsPullSpout extends BaseRichSpout {
    private static final Log logger = LogFactory.getLog(JmsPullSpout.class);
    private static final Log loggerRecovery = LogFactory.getLog(RecoveryTask.class);
    private static final int POLL_INTERVAL_MS = 50;
    private static final int DEFAULT_MESSAGE_TIMEOUT_SECS = 30;
    private static final int RECOVERY_DELAY_MS = 10;
    private final Serializable recoveryMutex = "RECOVERY_MUTEX";
    private final Serializable pullMessageMutex = "PULL_MESSAGE_MUTEX";
    private int jmsAcknowledgeMode = Session.AUTO_ACKNOWLEDGE;
    private JmsTupleProducer tupleProducer;
    private JmsProvider jmsProvider;
    private LinkedBlockingQueue<Message> queue;
    private TreeSet<JmsMessageID> toCommit;
    private Map<JmsMessageID, Message> pendingMessages;
    private long messageSquence = 0;
    private int maxQueueSize = 3000;
    private SpoutOutputCollector collector;
    private transient Connection connection;
    private transient Session session;
    private transient MessageConsumer consumer;
    private Timer pullMessageTimer = null;
    private boolean hasFailures = false;
    private Timer recoveryTimer = null;
    private long recoveryPeriodMs = -1;

    public JmsPullSpout() {
        super();
    }

    public JmsPullSpout(JSONObject config) {
        this();
    }

    /**
     * 转换消息消费确认模式
     *
     * @param deliveryMode 消息分发确认模式
     * @return 转换为字符串类型的消息分发确认模式
     */
    private static String toDeliveryModeString(int deliveryMode) {
        switch (deliveryMode) {
            case Session.AUTO_ACKNOWLEDGE:
                return "AUTO_ACKNOWLEDGE";
            case Session.CLIENT_ACKNOWLEDGE:
                return "CLIENT_ACKNOWLEDGE";
            case Session.DUPS_OK_ACKNOWLEDGE:
                return "DUPS_OK_ACKNOWLEDGE";
            default:
                return "UNKNOWN";

        }
    }

    /**
     * 获取JMS会话
     *
     * @return JMS会话
     */
    protected Session getSession() {
        return this.session;
    }

    /**
     * 判定是否延迟订阅
     *
     * @return 如果不采用自动确认消息消费，则为延迟订阅。
     */
    private boolean isDurableSubscription() {
        return (this.jmsAcknowledgeMode != Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * 获取消息消费确认模式
     *
     * @return 消息消费确认模式
     */
    public int getJmsAcknowledgeMode() {
        return this.jmsAcknowledgeMode;
    }

    /**
     * 设置消息消费确认模式
     *
     * @param mode 消息消费确认模式
     */
    public void setJmsAcknowledgeMode(final int mode) {
        switch (mode) {
            case Session.AUTO_ACKNOWLEDGE:
            case Session.CLIENT_ACKNOWLEDGE:
            case Session.DUPS_OK_ACKNOWLEDGE:
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknow Acknowledge mode: %d (See javax.jms.Session for valid values).)", mode));
        }
        this.jmsAcknowledgeMode = mode;
    }

    /**
     * 设置JMS提供者对象
     *
     * @param provider JMS提供者对象
     */
    public void setJmsProvider(final JmsProvider provider) {
        this.jmsProvider = provider;
    }

    /**
     * 设置JMS消息转换为元组的生产器
     *
     * @param producer 元组生产器
     */
    public void setJmsTupleProducer(JmsTupleProducer producer) {
        this.tupleProducer = producer;
    }

    /**
     * 判定是否发生错误，如果发生错误，则进行恢复操作。
     *
     * @return 如果发生错误，则返回true；否则返回false。
     */
    public boolean hasFailures() {
        return this.hasFailures;
    }

    /**
     * 设置修复完成标记
     */
    protected void recovered() {
        this.hasFailures = false;
    }

    /**
     * 从JMS服务器上拉取消息，并进行缓存消费。
     */
    protected void pullMessage() {
        if (this.consumer == null) {
            if (logger.isErrorEnabled()) {
                logger.error("JMS Consumer not be initialized.");
            }
            return;
        }
        try {
            Message message = null;
            while ((message = consumer.receive()) != null) {
                this.queue.offer(message);
                if (this.queue.size() > maxQueueSize) {
                    return;
                }
            }
        } catch (JMSException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error receiving message.", ex);
            }
        }
    }

    /**
     * 设置消息恢复延迟间隔时间，单位为毫秒。
     *
     * @param period 间隔时间
     */
    public void setRecoveryDelayMs(long period) {
        this.recoveryPeriodMs = period;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichSpout#open(Map, TopologyContext, SpoutOutputCollector)
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        if (this.jmsProvider == null) {
            throw new IllegalArgumentException("JMS provider has not been set.");
        }
        if (this.tupleProducer == null) {
            throw new IllegalArgumentException("JMS Tuple Producer has not been set.");
        }
        Number topologyTimeoutConf = ((Number) conf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS));
        Long topologyTimeout = topologyTimeoutConf != null ? topologyTimeoutConf.longValue() : DEFAULT_MESSAGE_TIMEOUT_SECS;
        if ((TimeUnit.SECONDS.toMillis(topologyTimeout)) > this.recoveryPeriodMs) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("*** WARNING ***: Recovery period (%d ms.) is less then the configured " +
                                "'topology.message.timeout.secs' of %d secs. This could lead to a message replay flood!",
                        this.recoveryPeriodMs, topologyTimeout));
            }
        }
        this.queue = new LinkedBlockingQueue<>();
        this.toCommit = new TreeSet<>();
        this.pendingMessages = new HashMap<>();
        this.collector = collector;
        try {
            ConnectionFactory cf = this.jmsProvider.connectionFactory();
            Destination dest = this.jmsProvider.destination();
            this.connection = cf.createConnection();
            this.session = connection.createSession(false, this.jmsAcknowledgeMode);
            this.consumer = session.createConsumer(dest);
            this.connection.start();
            // 启动pull message timer
            this.pullMessageTimer = new Timer();
            this.pullMessageTimer.scheduleAtFixedRate(new PullMessageTask(), 0, POLL_INTERVAL_MS);
            // 如果必要，启动recovery timer
            if (this.isDurableSubscription() && this.recoveryPeriodMs > 0) {
                this.recoveryTimer = new Timer();
                this.recoveryTimer.scheduleAtFixedRate(new RecoveryTask(), RECOVERY_DELAY_MS, this.recoveryPeriodMs);
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error creating JMS Connection.", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichSpout#close()
     */
    @Override
    public void close() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing JMS connection.");
            }
            this.consumer.close();
            this.session.close();
            this.connection.close();
        } catch (JMSException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Error closing JMS connection.", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichSpout#nextTuple()
     */
    @Override
    public void nextTuple() {
        Message message = this.queue.poll();
        if (message == null) {
            Utils.sleep(POLL_INTERVAL_MS);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("sending tuple: %s.", message));
            }
            try {
                Values vals = this.tupleProducer.toTuple(message);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Requested deliveryMode: %s.", toDeliveryModeString(message.getJMSDeliveryMode())));
                    logger.debug(String.format("Our deliveryMode: %s", toDeliveryModeString(this.jmsAcknowledgeMode)));
                }
                if (this.isDurableSubscription()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Requesting acks.");
                    }
                    JmsMessageID messageID = new JmsMessageID(this.messageSquence++, message.getJMSMessageID());
                    this.collector.emit(vals, messageID);
                    this.pendingMessages.put(messageID, message);
                    this.toCommit.add(messageID);
                } else {
                    this.collector.emit(vals);
                }
            } catch (JMSException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Unable to convert JMS message: %s.", message));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichSpout#ack(Object)
     */
    @Override
    public void ack(Object msgId) {
        Message message = this.pendingMessages.remove(msgId);
        JmsMessageID oldest = this.toCommit.first();
        if (msgId.equals(oldest)) {
            if (message != null) {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Committing...");
                    }
                    message.acknowledge();
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("JMS Message acked: %s.", msgId));
                    }
                    this.toCommit.remove(msgId);
                } catch (JMSException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Error acknowledging JMS message: %s.", msgId), ex);
                    }
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Couldn't acknoledge unknown JMS message ID: %s.", msgId));
                }
            }
        } else {
            this.toCommit.remove(msgId);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichSpout#fail(Object)
     */
    @Override
    public void fail(Object msgId) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.format("Message failed: %s.", msgId));
        }
        this.pendingMessages.clear();
        this.toCommit.clear();
        synchronized (this.recoveryMutex) {
            this.hasFailures = true;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichSpout#declareOutputFields(OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        this.tupleProducer.declareOutputFields(declarer);
    }

    /**
     * 在延迟订阅模式下，失败消息的定时恢复任务
     */
    private class RecoveryTask extends TimerTask {
        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (JmsPullSpout.this.recoveryMutex) {
                if (JmsPullSpout.this.hasFailures()) {
                    try {
                        if (loggerRecovery.isInfoEnabled()) {
                            loggerRecovery.info("Recovering from a message failure.");
                        }
                        JmsPullSpout.this.getSession().recover();
                        JmsPullSpout.this.recovered();
                    } catch (JMSException ex) {
                        if (loggerRecovery.isWarnEnabled()) {
                            loggerRecovery.warn("Couldn't recover jms session.", ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * 消息拉取的定时任务，以非常小的拉取延时间隔从JMS服务器上获取数据。
     */
    private class PullMessageTask extends TimerTask {
        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (JmsPullSpout.this.pullMessageMutex) {
                if (JmsPullSpout.this.queue.isEmpty()) {
                    JmsPullSpout.this.pullMessage();
                } else {
                    Utils.sleep(POLL_INTERVAL_MS);
                }

            }
        }
    }
}
