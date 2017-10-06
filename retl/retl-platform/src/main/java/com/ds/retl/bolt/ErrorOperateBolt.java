package com.ds.retl.bolt;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.error.ETLError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 2017/9/7.
 */
public class ErrorOperateBolt extends BaseRichBolt {
    private static final Log logger = LogFactory.getLog(ErrorOperateBolt.class);
    public static final String STREAM_NAME = "error-stream";

    private OutputCollector collector = null;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        JSONObject managedJson = (JSONObject) input.getValueByField("managedJson");
        JSONObject data = (JSONObject) input.getValueByField("data");
        List<ETLError> errors = (List<ETLError>)input.getValueByField("errors");
        if (errors == null || errors.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There no errors, but must has any errors in here.");
            }
        }
        managedJson.put("_type", "error");
        JSONObject json = new JSONObject();
        json.put("data", data);
        json.put("errors", errors);
        managedJson.put("_errorTime", new Date().getTime());
        this.collector.emit(input, new Values(managedJson, json));
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("managedJson", "data"));
    }
}
