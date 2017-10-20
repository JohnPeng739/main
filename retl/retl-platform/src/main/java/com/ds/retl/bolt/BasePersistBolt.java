package com.ds.retl.bolt;

import com.alibaba.fastjson.JSON;
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
 * 用于持久化存储数据的Bolt抽象类。
 *
 * @author : john.peng created on date : 2017/9/13
 */
public abstract class BasePersistBolt extends BaseRichBolt {
    private static final Log logger = LogFactory.getLog(BasePersistBolt.class);

    private JSONObject configuration = null;
    private OutputCollector collector = null;

    /**
     * 默认的构造函数
     */
    public BasePersistBolt() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param configuration 相关的初始化配置信息
     */
    public BasePersistBolt(JSONObject configuration) {
        this();
        this.configuration = configuration;
    }

    /**
     * 将管理对象中相关的操作时间数据转储到标准数据对象中，便于后续根据映射将操作时间持久化到媒体中。
     *
     * @param managedJson 管理对象
     * @param data        数据对象
     */
    protected void saveMangedTimeData(JSONObject managedJson, JSONObject data) {
        data.put("_structuredTime", managedJson.getLongValue("_structuredTime"));
        data.put("_validatedTime", managedJson.getLongValue("_validatedTime"));
        data.put("_transformedTime", managedJson.getLongValue("_transformedTime"));
        data.put("_errorTime", managedJson.getLongValue("_errorTime"));
        data.put("_persistedTime", new Date().getTime());
    }

    /**
     * 初始化Bolt
     *
     * @param stormConf     拓扑传递的配置信息
     * @param configuration 初始化配置信息
     * @see #prepare(Map, TopologyContext, OutputCollector)
     */
    public abstract void initialize(Map stormConf, JSONObject configuration);

    /**
     * 将数据持久化到媒体（各种数据库）中
     *
     * @param managedJson 管理对象
     * @param data        数据对象
     * @throws Exception 保存过程中发生的异常
     */
    public abstract void saveData2Db(JSONObject managedJson, JSONObject data) throws Exception;

    /**
     * 将错误信息持久化到媒体（各种数据库）中
     *
     * @param managedJson 管理对象
     * @param data        数据对象
     * @param errors      错误信息列表
     * @throws Exception 保存过程中发生的异常
     */
    public abstract void saveError2Db(JSONObject managedJson, JSONObject data, JSONArray errors) throws Exception;

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#prepare(Map, TopologyContext, OutputCollector)
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        this.initialize(stormConf, this.configuration);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#execute(Tuple)
     */
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

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#declareOutputFields(OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
