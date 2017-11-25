package com.ds.retl.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.error.StructureError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.mx.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 将JSON文本数据转换为标准的JSONObject对象，便于后续进行处理。
 * 一般情况下，所有字段数据均在JSONObject对象中作为顶级字段读写，如：
 * {bh: "23123", type: "red"}
 *
 * @author : john.peng created on date : 2017/9/7
 */
public class StructureBolt extends BaseRichBolt {
    private static final Log logger = LogFactory.getLog(StructureBolt.class);

    private OutputCollector collector = null;

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#prepare(Map, TopologyContext, OutputCollector)
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#execute(Tuple)
     */
    @Override
    public void execute(Tuple input) {
        JSONObject managedJson = (JSONObject) input.getValueByField("managedJson");
        String json = input.getStringByField("json");
        if (StringUtils.isBlank(json)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The json from tuple is blank, stream: %s, task: %d, component: %s, message id: %s.",
                        input.getSourceStreamId(), input.getSourceTask(), input.getSourceComponent(),
                        input.getMessageId().toString()));
            }
            this.collector.ack(input);
            return;
        }

        JSONObject data = JSON.parseObject(json);
        try {
            managedJson.put("_structuredTime", new Date().getTime());
            this.collector.emit(input, new Values(managedJson, data));
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Parse JSONObject fail: %s.", json), ex);
            }
            this.collector.emit(ErrorOperateBolt.STREAM_NAME, input,
                    new Values(managedJson, data, Arrays.asList(new StructureError("JSON数据格式解析错误", json))));
        } finally {
            this.collector.ack(input);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#declareOutputFields(OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("managedJson", "data"));
        declarer.declareStream(ErrorOperateBolt.STREAM_NAME, new Fields("managedJson", "data", "errors"));
    }
}
