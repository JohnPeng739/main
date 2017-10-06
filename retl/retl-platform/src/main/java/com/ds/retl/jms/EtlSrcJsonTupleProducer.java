package com.ds.retl.jms;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsTupleProducer;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Created by john on 2017/9/6.
 */
public class EtlSrcJsonTupleProducer implements JmsTupleProducer {
    private static final Log logger = LogFactory.getLog(EtlSrcJsonTupleProducer.class);

    private JSONObject managedJson = null;

    public EtlSrcJsonTupleProducer() {
        super();
        managedJson = new JSONObject();
        managedJson.put("source", "JMS");
    }

    @Override
    public Values toTuple(Message msg) throws JMSException {
        if(msg instanceof TextMessage){
            String json = ((TextMessage) msg).getText();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("RECEIVE TEXT MESSAGE: %s.", json));
            }
            return new Values(managedJson, json);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("RECEIVE NON TEXT MESSAGE, id: %s, type: %s.",
                        msg.getJMSMessageID(), msg.getJMSType()));
            }
            return null;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("managedJson", "json"));
    }
}
