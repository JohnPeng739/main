package com.ds.retl.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsTupleProducer;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.mx.StringUtils;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 采集数据的元组产生器，将从JMS接收的的消息转换为字符串对象类型的元组进行发送。
 *
 * @author : john.peng created on date : 2017/9/6
 */
public class EtlSrcJsonTupleProducer implements JmsTupleProducer {
    private static final Log logger = LogFactory.getLog(EtlSrcJsonTupleProducer.class);

    private JSONObject managedJson = null;

    private Set<String> fields = null;
    private Map<String, String> fieldsTransform = null;

    /**
     * 默认的构造函数
     */
    public EtlSrcJsonTupleProducer() {
        super();
        managedJson = new JSONObject();
        managedJson.put("source", "JMS");
        this.fields = new HashSet<>();
        this.fieldsTransform = new HashMap<>();
    }

    /**
     * 默认的构造函数
     *
     * @param config 配置信息
     */
    public EtlSrcJsonTupleProducer(JSONObject config) {
        this();

        if (config.containsKey("fields")) {
            String fieldsStr = config.getString("fields");
            if (!StringUtils.isBlank(fieldsStr)) {
                String[] field = fieldsStr.split(",");
                for (String f : field) {
                    this.fields.add(f);
                }
            }
        }
        if (config.containsKey("fieldTransform")) {
            this.fieldsTransform = config.getObject("fieldTransform", Map.class);
        }
    }

    /**
     * 根据字段列表和字段名转换规格对输入的JSON数据进行预处理
     *
     * @param json 输入的JSON文本
     * @return 经过预处理的JSON文本
     * @throws Exception 处理过程中发生的异常
     */
    private String preparedJsonData(String json) throws Exception {
        JSONObject src = JSON.parseObject(json);
        JSONObject tar = new JSONObject();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("src JSON String: %s.", json));
        }
        for (String field : fields) {
            if (!src.containsKey(field)) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The field[%s] not exist, json: %s.", field, json));
                }
            }
            Object value = src.get(field);
            if (fieldsTransform.containsKey(field)) {
                tar.put(fieldsTransform.get(field), value);
            } else {
                tar.put(field, value);
            }
        }
        return tar.toJSONString();
    }

    /**
     * {@inheritDoc}
     *
     * @see JmsTupleProducer#toTuple(Message)
     */
    @Override
    public Values toTuple(Message msg) throws JMSException {
        if (msg instanceof TextMessage) {
            String json = ((TextMessage) msg).getText();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("RECEIVE TEXT MESSAGE: %s.", json));
            }
            try {
                String prepared = preparedJsonData(json);
                return new Values(managedJson, prepared);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Prepared the JSON data fail, json: %s.", json), ex);
                }
                return null;
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("RECEIVE NON TEXT MESSAGE, id: %s, type: %s.",
                        msg.getJMSMessageID(), msg.getJMSType()));
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see JmsTupleProducer#declareOutputFields(OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("managedJson", "json"));
    }
}
