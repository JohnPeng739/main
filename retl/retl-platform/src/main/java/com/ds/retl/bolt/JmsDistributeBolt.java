package com.ds.retl.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.jms.JmsMultiProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsMessageProducer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Tuple;
import org.mx.StringUtils;

import javax.jms.*;
import java.io.Serializable;
import java.lang.IllegalStateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据条件可以进行数据分发存储的JMS持久化Bolt
 * 配置：
 * {
 * conditions: [{
 * destinateName: "tar-jjd",
 * isTopic: false,
 * condition: {
 * field: "tableName",
 * operate: "equals",
 * value: "jjd"
 * },
 * includes: ["data", "managedJson"],
 * }, {
 * destinateName: "tar-cjd",
 * isTopic: false,
 * condition: {
 * field: "tableName",
 * operate: "startsWith",
 * value: "cjd"
 * },
 * includes: ["data", "managedJson"]
 * }]
 * }
 *
 * @author : john.peng created on date : 2017/11/9
 */
public class JmsDistributeBolt extends BaseRichBolt {
    private static final Log logger = LogFactory.getLog(JmsDistributeBolt.class);

    // javax.jms objects
    private Connection connection;
    private Session session;
    private JmsMultiProvider jmsMultiProvider;
    private OutputCollector collector;

    private Map<String, MessageDistinateConfig> destinateConfigs;

    public JmsDistributeBolt(JSONArray destinations) {
        super();
        this.destinateConfigs = new HashMap<>();
        for (int index = 0; index < destinations.size(); index++) {
            JSONObject config = destinations.getJSONObject(index);
            String destinateName = config.getString("destinateName");
            MessageDistinateConfig distinateConfig = new MessageDistinateConfig(config);
            distinateConfig.jmsProducer = new JmsMessageProducer() {

                @Override
                public Message toMessage(Session session, ITuple input) throws JMSException {
                    JSONObject managedJson = (JSONObject) input.getValueByField("managedJson");
                    JSONObject data = (JSONObject) input.getValueByField("data");
                    if (managedJson == null || data == null) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("The ManagedJson or Data is null.");
                        }
                        return null;
                    }
                    JSONObject preparedData = new JSONObject();
                    final List<String> includes = distinateConfig.includes;
                    for(String key : data.keySet()) {
                        if (includes.isEmpty() || includes.contains(key)) {
                            preparedData.put(key, data.get(key));
                        } else if (includes.contains(String.format("%s.*", key))) {
                            // 将下级所有的数据加入到数据对象中，级别升一级
                            Map<String, Object> map = data.getObject(key, Map.class);
                            preparedData.putAll(map);
                        }
                    }
                    data = new JSONObject();
                    data.put("data", preparedData);
                    data.put("managedJson", managedJson);
                    return session.createTextMessage(data.toJSONString());
                }
            };
            this.destinateConfigs.put(destinateName, distinateConfig);
        }
    }

    public void setJmsMultiProvider(JmsMultiProvider provider) {
        this.jmsMultiProvider = provider;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#prepare(Map, TopologyContext, OutputCollector)
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        if (this.jmsMultiProvider == null) {
            throw new IllegalStateException("JMS Provider not set.");
        }
        this.collector = collector;
        if (logger.isDebugEnabled()) {
            logger.debug("Connecting JMS..");
        }
        try {
            ConnectionFactory cf = this.jmsMultiProvider.connectionFactory();
            Map<String, Destination> dests = this.jmsMultiProvider.destinations();
            this.connection = cf.createConnection();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dests.keySet().forEach(destinateName -> {
                try {
                    MessageProducer messageProducer = session.createProducer(dests.get(destinateName));
                    destinateConfigs.get(destinateName).producer = messageProducer;
                } catch (JMSException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Error creating JMS destinate: %s.", destinateName), ex);
                    }
                }
            });

            connection.start();
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Error creating JMS connection.", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#execute(Tuple)
     */
    @Override
    public void execute(Tuple input) {
        try {
            JSONObject managedJson = (JSONObject) input.getValueByField("managedJson");
            JSONObject data = (JSONObject) input.getValueByField("data");
            for (MessageDistinateConfig config : this.destinateConfigs.values()) {
                if (config.needDistributed(data)) {
                    Message msg = config.jmsProducer.toMessage(this.session, input);
                    if (msg != null) {
                        config.producer.send(msg);
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ACKing tuple: " + input);
            }
            this.collector.ack(input);
        } catch (JMSException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Operate input fail, " + input, ex);
            }
            this.collector.fail(input);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#cleanup()
     */
    @Override
    public void cleanup() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("clean the JMS connection.");
            }
            this.session.close();
            this.connection.close();
        } catch (JMSException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Clean the JMS connection fail.", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#declareOutputFields(OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // do nothing
    }

    public class MessageDistinateConfig implements Serializable {
        private String destinateName;
        private MessageProducer producer;
        private JmsMessageProducer jmsProducer;
        private JSONObject condition;
        private List<String> includes;

        public MessageDistinateConfig(JSONObject config) {
            super();
            this.destinateName = config.getString("destinateName");
            this.condition = config.getJSONObject("condition");
            this.includes = config.getJSONArray("includes").toJavaList(String.class);
        }

        public boolean needDistributed(JSONObject data) {
            if (this.condition == null) {
                return true;
            }
            String field = this.condition.getString("field"),
                    operate = this.condition.getString("operate"),
                    value = this.condition.getString("value");
            if (StringUtils.isBlank(field) || StringUtils.isBlank(operate)) {
                return true;
            }
            String dataValue = data.getString(field);
            if (dataValue == null) {
                dataValue = "";
            }
            switch (operate) {
                case "equals":
                    return dataValue.equals(value);
                case "startsWith":
                    return dataValue.startsWith(value);
                case "endsWith":
                    return dataValue.endsWith(value);
                default:
                    return false;
            }
        }
    }
}
