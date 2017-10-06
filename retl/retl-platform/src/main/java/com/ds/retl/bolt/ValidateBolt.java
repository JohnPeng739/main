package com.ds.retl.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;
import com.ds.retl.validate.ValidateFunc;
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
 * Created by john on 2017/9/7.
 */
public class ValidateBolt extends BaseRichBolt {
    private static final Log logger = LogFactory.getLog(ValidateBolt.class);

    private OutputCollector collector = null;

    private Map<String, RecordColumn> columns = null;
    private Map<String, List<JSONObject>> validateConfigs = null;
    private Map<String, ValidateFunc> validateRules = null;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        // 记录中每个字段的定义
        String jsonStr = (String)stormConf.get("columns");
        Map<String, RecordColumn> columns = new HashMap<>();
        if (!StringUtils.isBlank(jsonStr)) {
            JSONArray jsonColumns = JSON.parseArray(jsonStr);
            for (int index = 0; index < jsonColumns.size(); index++) {
                RecordColumn column = JSON.parseObject(jsonColumns.getJSONObject(index).toJSONString(), RecordColumn.class);
                if (column != null) {
                    columns.put(column.getName(), column);
                }
            }
        }
        this.columns = columns;

        // 记录中每个字段的校验定义
        this.validateConfigs = new HashMap<>();
        this.validateRules = new HashMap<>();
        String validateStr = (String) stormConf.get("validates");
        JSONObject validates = JSON.parseObject(validateStr);
        // 从配置中转换并缓存校验器
        if (validates == null || validates.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("May not define any validate rule.");
            }
        } else {
            validates.keySet().forEach(key -> {
                JSONArray rules = validates.getJSONArray(key);
                List<JSONObject> configs = new ArrayList<>();
                rules.forEach(rule -> {
                    JSONObject json = (JSONObject) rule;
                    configs.add(json);
                    // 初始化并缓存校验函数
                    String funcName = json.getString("type");
                    try {
                        Class<ValidateFunc> clazz = (Class<ValidateFunc>) Class.forName(String
                                .format("com.ds.retl.validate.%sFunc", funcName));
                        this.validateRules.put(funcName, clazz.newInstance());
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Initialize validate func fail, func name: %s.", funcName));
                        }
                    }
                });
                this.validateConfigs.put(key, configs);
            });
        }
    }

    @Override
    public void execute(Tuple input) {
        JSONObject managedJson = (JSONObject) input.getValueByField("managedJson");
        JSONObject data = (JSONObject) input.getValueByField("data");
        if (data == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The data field from tuple is null, validate operate will be ignored.");
            }
            this.collector.ack(input);
            return;
        }
        try {
            List<ValidateError> errors = new ArrayList<>();
            this.validateConfigs.keySet().forEach(key -> {
                RecordColumn column = this.columns.get(key);
                this.validateConfigs.get(key).forEach(config -> {
                    String type = config.getString("type");
                    ValidateFunc func = this.validateRules.get(type);
                    if (func == null) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(String.format("The ValidateFunc[%s] not exist for column[%s].", type, key));
                        }
                        errors.add(new ValidateError(key, String.format("字段[%s]的校验方法[%s]不存在", key, type)));
                    } else {
                        ValidateError error = func.validate(column, config, data);
                        if (error != null) {
                            errors.add(error);
                        }
                    }
                });
            });
            if (errors.isEmpty()) {
                // 校验无错误
                managedJson.put("_validatedTime", new Date().getTime());
                this.collector.emit(input, new Values(managedJson, data));
            } else {
                // 校验有错误
                this.collector.emit(ErrorOperateBolt.STREAM_NAME, input, new Values(managedJson, data, errors));
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Validate fail, stream: %s, task: %d, component: %s, message id: %s.",
                        input.getSourceStreamId(), input.getSourceTask(), input.getSourceComponent(),
                        input.getMessageId().toString()), ex);
            }
            this.collector.emit(ErrorOperateBolt.STREAM_NAME, input, new Values(managedJson, data,
                    Arrays.asList(new ValidateError(ex.getMessage(), data))
            ));
        } finally {
            this.collector.ack(input);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("managedJson", "data"));
        declarer.declareStream(ErrorOperateBolt.STREAM_NAME, new Fields("managedJson", "data", "errors"));
    }
}
