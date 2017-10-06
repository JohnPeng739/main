package com.ds.retl.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Date;
import java.util.Map;

/**
 * Created by john on 2017/9/13.
 */
public abstract class BasePersistBolt extends BaseRichBolt {
    private static final Log logger = LogFactory.getLog(BasePersistBolt.class);

    private JSONObject configuration = null;
    private OutputCollector collector = null;

    public BasePersistBolt() {
        super();
    }

    public BasePersistBolt(JSONObject configuration) {
        this();
        this.configuration = configuration;
    }

    protected void saveMangedTimeData(JSONObject managedJson, JSONObject data) {
        data.put("_structuredTime", managedJson.getLongValue("_structuredTime"));
        data.put("_validatedTime", managedJson.getLongValue("_validatedTime"));
        data.put("_transformedTime", managedJson.getLongValue("_transformedTime"));
        data.put("_errorTime", managedJson.getLongValue("_errorTime"));
        data.put("_persistedTime", new Date().getTime());
    }

    public abstract void initialize(Map stormConf, JSONObject configuration);

    public abstract void saveData2Db(JSONObject managedJson, JSONObject data) throws Exception;

    public abstract void saveError2Db(JSONObject managedJson, JSONObject data, JSONArray errors) throws Exception;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        this.initialize(stormConf, this.configuration);
    }

    @Override
    public void execute(Tuple input) {
        JSONObject managedJson = (JSONObject) input.getValueByField("managedJson");
        JSONObject data = (JSONObject) input.getValueByField("data");
        String type = managedJson.getString("_type");
        switch (type) {
            case "data":
                try {
                    saveData2Db(managedJson, data);
                } catch (Exception ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Save data fail.", ex);
                    }
                    this.collector.fail(input);
                }
                break;
            case "error":
                JSONArray errors = data.getJSONArray("errors");
                JSONObject src = data.getJSONObject("data");
                try {
                    saveError2Db(managedJson, src, errors);
                } catch (Exception ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Save errors fail.", ex);
                    }
                    this.collector.fail(input);
                }
                break;
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported data type: %s.", type));
                }
                break;
        }
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
