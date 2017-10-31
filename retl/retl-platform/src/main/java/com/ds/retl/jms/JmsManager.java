package com.ds.retl.jms;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.spout.JmsPullSpout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsMessageProducer;
import org.apache.storm.jms.JmsProvider;
import org.apache.storm.jms.bolt.JmsBolt;
import org.apache.storm.jms.spout.JmsSpout;
import org.apache.storm.tuple.ITuple;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * JMS连接管理器
 *
 * @author : john.peng created on date : 2017/9/6
 */
public class JmsManager {
    private static final Log logger = LogFactory.getLog(JmsManager.class);

    /**
     * 创建一个推送型JMS数据采集器Spout
     *
     * @param supported 支持的类型枚举
     * @param config    初始化配置信息
     * @param isPersist 是否用于存储类拓扑
     * @return 创建好的JmsSpout对象
     * @throws JMSException 创建过程中发生的异常
     */
    public static JmsSpout createJmsSpout(Supported supported, JSONObject config, boolean isPersist) throws JMSException {
        if (config == null) {
            throw new NullPointerException("The Jms config is null.");
        }
        JmsSpout spout = new JmsSpout();
        spout.setJmsProvider(createProvider(supported, config));
        String producer = config.getString("producer");
        if ("JSON".equalsIgnoreCase(producer)) {
            spout.setJmsTupleProducer(isPersist ? new EtlPersistJsonTupleProducer() : new EtlSrcJsonTupleProducer());
        } else {
            throw new UnsupportedOperationException(String.format("Not supported producer: %s.", producer));
        }
        spout.setJmsAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return spout;
    }

    /**
     * 创建一个拉取型JMS数据采集器Spout，安全可靠，推荐使用
     *
     * @param supported 支持的JMS类型
     * @param config    初始化配置信息
     * @param isPersist 是否用于存储类拓扑
     * @return 创建好的JmsSpout对象
     * @throws JMSException 创建过程中发生的异常
     */
    public static JmsPullSpout createJmsPullSpout(Supported supported, JSONObject config, boolean isPersist) throws JMSException {
        if (config == null) {
            throw new NullPointerException("The Jms config is null.");
        }
        JmsPullSpout spout = new JmsPullSpout();
        spout.setJmsProvider(createProvider(supported, config));
        String producer = config.getString("producer");
        if ("JSON".equalsIgnoreCase(producer)) {
            spout.setJmsTupleProducer(isPersist ? new EtlPersistJsonTupleProducer() : new EtlSrcJsonTupleProducer(config));
        } else {
            throw new UnsupportedOperationException(String.format("Not supported producer: %s.", producer));
        }
        spout.setJmsAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return spout;
    }

    /**
     * 创建一个JMS数据持久化Bolt，用于存储中间数据
     *
     * @param supported 支持的JMS类型
     * @param config    初始化配置信息
     * @return 创建好的数据持久化器Bolt
     * @throws JMSException 创建过程中发生的异常
     */
    public static JmsBolt createJmsBolt(Supported supported, JSONObject config) throws JMSException {
        if (config == null) {
            throw new NullPointerException("The Jms config is null.");
        }
        JmsBolt bolt = new JmsBolt();
        bolt.setJmsProvider(createProvider(supported, config));
        bolt.setJmsMessageProducer(new JmsMessageProducer() {
            @Override
            public Message toMessage(Session session, ITuple input) throws JMSException {
                // 从元组中读取json字段的值，然后作为一个TextMessage发送
                JSONObject managedJson = (JSONObject) input.getValueByField("managedJson");
                JSONObject data = (JSONObject) input.getValueByField("data");
                if (managedJson == null || data == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("The ManagedJson or Data is null.");
                    }
                    return null;
                }
                JSONObject json = new JSONObject();
                json.put("managedJson", managedJson);
                json.put("data", data);
                return session.createTextMessage(json.toJSONString());
            }
        });
        return bolt;
    }

    /**
     * 创建一个JMS提供者
     *
     * @param supported 支持的JMS类型
     * @param config    初始化配置信息
     * @return 创建好的JMS提供者
     * @throws JMSException 创建过程中发生的异常
     */
    private static JmsProvider createProvider(Supported supported, JSONObject config) throws JMSException {
        switch (supported) {
            case ACTIVEMQ:
                return createActiveMQProvider(config);
            case JNDI:
                return createJndiProvider(config);
            default:
                throw new UnsupportedOperationException(String.format("Not support: %s.", supported));
        }
    }

    /**
     * 创建一个Apache ActiveMQ类型的JMS提供者
     *
     * @param config 初始化配置信息
     * @return 创建好的JMS提供者对象
     * @throws JMSException 创建过程中发生的异常
     */
    private static JmsProvider createActiveMQProvider(JSONObject config) throws JMSException {
        String connection = config.getString("connection"),
                user = config.getString("user"),
                password = config.getString("password"),
                destinateName = config.getString("destinateName");
        boolean isTopic = config.getBoolean("isTopic");
        return new ActiveMqJmsProvider(connection, user, password, destinateName, isTopic);
    }

    /**
     * 创建一个容器管理的JNDI类型的JMS提供者
     *
     * @param config 初始化配置信息
     * @return 创建好的JMS提供者对象
     * @throws JMSException 创建过程中发生的异常
     */
    private static JmsProvider createJndiProvider(JSONObject config) throws JMSException {
        String connectionFactoryName = config.getString("connectionFactoryName"),
                destinateName = config.getString("destinateName");
        return new JndiJmsProvider(connectionFactoryName, destinateName);
    }

    /**
     * 支持的JMS类型
     *
     * @author : john.peng created on date : 2017/9/6
     */
    public enum Supported {
        /**
         * ActiveMQ
         */
        ACTIVEMQ,
        /**
         * 容器管理的JNDI
         */
        JNDI
    }
}
