package com.ds.retl.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;
import com.ds.retl.transform.TransformFunc;
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

import java.util.*;

/**
 * 数据转换Bolt类，能够根据预定义规则将数据进行变形处理。
 *
 * @author : john.peng created on date : 2017/9/7
 */
public class TransformBolt extends BaseRichBolt {
    private static final Log logger = LogFactory.getLog(TransformBolt.class);

    private OutputCollector collector = null;

    private Map<String, RecordColumn> columns = null;
    private Map<String, JSONObject> transformConfigs = null;
    private Map<String, TransformFunc> transformRules = null;

    /**
     * {@inheritDoc}
     *
     * @see BaseRichBolt#prepare(Map, TopologyContext, OutputCollector)
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        // 记录中每个字段的定义
        String jsonStr = (String) stormConf.get("columns");
        Map<String, RecordColumn> columns = new HashMap<>();
        if (!StringUtils.isBlank(jsonStr)) {
            JSONArray jsonColumns = com.alibaba.fastjson.JSON.parseArray(jsonStr);
            for (int index = 0; index < jsonColumns.size(); index++) {
                RecordColumn column = JSON.parseObject(jsonColumns.getJSONObject(index).toJSONString(), RecordColumn.class);
                if (column != null) {
                    columns.put(column.getName(), column);
                }
            }
        }
        this.columns = columns;

        // 记录中每个字段的转换定义
        this.transformConfigs = new HashMap<>();
        this.transformRules = new HashMap<>();
        String transformStr = (String) stormConf.get("transforms");
        JSONObject transforms = JSON.parseObject(transformStr);
        // 从配置中转换并缓存校验器
        if (transforms == null || transforms.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("May not define any transform rule.");
            }
        } else {
            transforms.keySet().forEach(key -> {
                JSONObject rule = transforms.getJSONObject(key);
                // 初始化并缓存转换函数
                String funcName = rule.getString("type");
                try {
                    Class<TransformFunc> clazz = (Class<TransformFunc>) Class.forName(String
                            .format("com.ds.retl.transform.%sFunc", funcName));
                    this.transformRules.put(funcName, clazz.newInstance());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Initialize transform func fail, func name: %s.", funcName));
                    }
                }
                this.transformConfigs.put(key, rule);
            });
        }
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
        if (data == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The data field from tuple is null, transform operate will be ignored.");
            }
            this.collector.ack(input);
            return;
        }
        try {
            List<TransformError> errors = new ArrayList<>();
            this.transformConfigs.keySet().forEach(key -> {
                RecordColumn column = this.columns.get(key);
                JSONObject config = this.transformConfigs.get(key);
                String type = config.getString("type");
                TransformFunc func = this.transformRules.get(type);
                if (func == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("The TransformFunc[%s] not existInCache for column[%s].", type, key));
                    }
                    errors.add(new TransformError(key, String.format("字段[%s]的转换方法[%s]不存在", key, type)));
                } else {
                    List<TransformError> errs = func.transform(columns, column, config, data);
                    if (errs != null) {
                        errors.addAll(errs);
                    }
                }
            });
            if (errors.isEmpty()) {
                // 转换无错误
                managedJson.put("_transformedTime", new Date().getTime());
                managedJson.put("_type", "data");
                this.collector.emit(input, new Values(managedJson, data));
            } else {
                // 转换有错误
                this.collector.emit(ErrorOperateBolt.STREAM_NAME, input, new Values(managedJson, data, errors));
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Transform fail, stream: %s, task: %d, component: %s, message id: %s.",
                        input.getSourceStreamId(), input.getSourceTask(), input.getSourceComponent(),
                        input.getMessageId().toString()), ex);
            }
            this.collector.emit(ErrorOperateBolt.STREAM_NAME, input, new Values(managedJson, data,
                    Arrays.asList(new TransformError(ex.getMessage(), data))
            ));
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
