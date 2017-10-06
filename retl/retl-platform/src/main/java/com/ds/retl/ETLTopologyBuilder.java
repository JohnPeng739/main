package com.ds.retl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.ds.retl.bolt.*;
import com.ds.retl.jms.JmsManager;
import com.ds.retl.spout.JdbcSpout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.jms.spout.JmsSpout;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.mx.StringUtils;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 实时ETL计算拓扑构建器
 * <p>
 * Created by john on 2017/9/7.
 */
public class ETLTopologyBuilder {
    private static final Log logger = LogFactory.getLog(ETLTopologyBuilder.class);

    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            printUsage();
            return;
        }
        System.out.println("******************************************************");
        System.out.println("开始提交[实时数据ETL]拓扑到集群中......");
        String configFile = args[0];
        try(JSONReader reader = new JSONReader(new InputStreamReader(new FileInputStream(configFile)))) {
            String json = reader.readString();
            JSONObject config = JSON.parseObject(json);
            new ETLTopologyBuilder().buildTopology(true, config);
            System.out.println("提交拓扑成功。");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("提交拓扑失败！请查看输出日志。");
        }
        System.out.println("******************************************************");
    }

    private static void printUsage() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                ETLTopologyBuilder.class.getResourceAsStream("/readme.txt")))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void buildTopology(boolean isCluster, JSONObject json) {
        Config config = createConfig(json);

        TopologyBuilder builder = new TopologyBuilder();
        String topologyName = json.getString("name");
        if (StringUtils.isBlank(topologyName)) {
            String message = "Not define the name for the topology.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw new IllegalArgumentException(message);
        }

        // 创建定义的Spout
        JSONArray spoutArray = json.getJSONArray("spouts");
        createSpouts(spoutArray, builder);

        // 创建定义的Bolt
        JSONArray boltArray = json.getJSONArray("bolts");
        createBolts(boltArray, builder);

        // 提交ETL计算拓扑
        if (isCluster) {
            try {
                StormSubmitter.submitTopology(topologyName, config, builder.createTopology());
            } catch (AlreadyAliveException | InvalidTopologyException | AuthorizationException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Submit topology[%s] fail.", topologyName), ex);
                }
            }
        } else {
            try {
                LocalCluster localCluster = new LocalCluster();
                localCluster.submitTopology(topologyName, config, builder.createTopology());
                // 测试环境下阻塞，等待按任意键后退出
                System.out.println("测试环境下启动Storm集群成功，按任意键后退出......");
                System.in.read();
                localCluster.shutdown();
                System.out.println("正常退出Storm集群。");
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Submit topology[%s] fail in test environment.", topologyName), ex);
                }
            }
        }
    }

    private void createSpouts(JSONArray spoutArray, TopologyBuilder builder) {
        for (int index = 0; index < spoutArray.size(); index++) {
            JSONObject jsonSpout = spoutArray.getJSONObject(index);
            String name = jsonSpout.getString("name");
            String type = jsonSpout.getString("type");
            int parallelism = jsonSpout.getInteger("parallelism") != null ? jsonSpout.getInteger("parallelism") : 1;
            if (StringUtils.isBlank(name)) {
                String message = "Spout configuration error, not define the name of spout.";
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new IllegalArgumentException(message);
            }
            if (StringUtils.isBlank(type)) {
                String message = "Spout configuration error, not define the type of spout.";
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new IllegalArgumentException(message);
            }
            if ("JMS".equalsIgnoreCase(type) || "JMS_PULL".equalsIgnoreCase(type)) {
                // JMS Spout
                String method = jsonSpout.getString("method");
                JmsManager.Supported supported = JmsManager.Supported.valueOf(method);
                JSONObject configuration = jsonSpout.getJSONObject("configuration");
                try {
                    BaseRichSpout spout = "JMS".equalsIgnoreCase(type) ?
                            JmsManager.createJmsSpout(supported, configuration) :
                            JmsManager.createJmsPullSpout(supported, configuration);
                    builder.setSpout(name, spout, parallelism);
                } catch (JMSException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Create JmsSpout fail, name: %s, method: %s, configuration: %s.",
                                name, method, configuration.toJSONString()), ex);
                    }
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                }
            } else if ("JDBC".equalsIgnoreCase(type)) {
                JSONObject configuration = jsonSpout.getJSONObject("configuration");
                JdbcSpout spout = new JdbcSpout(configuration);
                // JdbcSpout的并行度必须设置为1，防止数据库死锁
                builder.setSpout(name, spout, 1);
            } else {
                throw new IllegalArgumentException(String.format("Unsupported spout's type: %s.", type));
            }
        }
    }

    private void createBolts(JSONArray boltArray, TopologyBuilder builder) {
        for (int index = 0; index < boltArray.size(); index++) {
            JSONObject jsonBolt = boltArray.getJSONObject(index);
            String name = jsonBolt.getString("name");
            String type = jsonBolt.getString("type");
            int parallelism = jsonBolt.getInteger("parallelism") != null ? jsonBolt.getInteger("parallelism") : 1;
            if (StringUtils.isBlank(name)) {
                String message = "Bolt configuration error, not define the name of bolt.";
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new IllegalArgumentException(message);
            }
            if (StringUtils.isBlank(type)) {
                String message = "Bolt configuration error, not define the type of bolt.";
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new IllegalArgumentException(message);
            }
            BaseRichBolt bolt = null;
            switch (type) {
                case "STRUCTURE":
                    bolt = new StructureBolt();
                    break;
                case "VALIDATE":
                    bolt = new ValidateBolt();
                    break;
                case "TRANSFORM":
                    bolt = new TransformBolt();
                    break;
                case "ERROR":
                    bolt = new ErrorOperateBolt();
                    break;
                case "JDBC":
                    bolt = new JdbcBolt(jsonBolt.getJSONObject("configuration"));
                    break;
                case "MONGO":
                    JSONObject mongoConf = jsonBolt.getJSONObject("configuration");
                    bolt = new MongoBolt(mongoConf);
                    break;
                case "JMS":
                    String method = jsonBolt.getString("method");
                    JmsManager.Supported supported = JmsManager.Supported.valueOf(method);
                    JSONObject jmsConf = jsonBolt.getJSONObject("configuration");
                    try {
                        bolt = JmsManager.createJmsBolt(supported, jmsConf);
                    } catch (JMSException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Create JmsBolt fail, name: %s, method: %s, configuration: %s.",
                                    name, method, jmsConf.toJSONString()), ex);
                        }
                        throw new IllegalArgumentException(ex.getMessage(), ex);
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unsupported bolt's type: %s.", type));
            }
            BoltDeclarer bd = builder.setBolt(name, bolt, parallelism);
            setBoltGrouping(name, bd, jsonBolt.getJSONArray("groups"));
        }
    }

    private void setBoltGrouping(String name, BoltDeclarer bd, JSONArray jsonGroups) {
        for (int index = 0; index < jsonGroups.size(); index++) {
            JSONObject jsonGroup = jsonGroups.getJSONObject(index);
            String source = jsonGroup.getString("source");
            if (StringUtils.isBlank(source)) {
                String message = String.format("The Bolt[%s] not define the group source.", name);
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new IllegalArgumentException(message);
            }
            String streamId = jsonGroup.getString("streamId");
            if (StringUtils.isBlank(streamId)) {
                streamId = "default";
            }
            String type = jsonGroup.getString("type");
            switch (type) {
                case "shuffle":
                    bd.shuffleGrouping(source, streamId);
                    break;
                case "direct":
                    bd.directGrouping(source, streamId);
                    break;
                case "global":
                    bd.directGrouping(source, streamId);
                    break;
                case "all":
                    bd.allGrouping(source, streamId);
                    break;
                case "none":
                    bd.noneGrouping(source, streamId);
                    break;
                case "localOrShuffle":
                    bd.localOrShuffleGrouping(source, streamId);
                    break;
                case "fields":
                case "partialKey":
                    String fieldStr = jsonGroup.getString("fields");
                    String[] fields = fieldStr.split(",");
                    if (fields == null || fields.length <= 0) {
                        String message = String.format("Not define the fields' list for Bolt[%s].", name);
                        if (logger.isErrorEnabled()) {
                            logger.error(message);
                        }
                        throw new IllegalArgumentException(message);
                    }
                    if ("fields".equalsIgnoreCase(type)) {
                        bd.fieldsGrouping(source, streamId, new Fields(fields));
                    } else {
                        bd.partialKeyGrouping(source, streamId, new Fields(fields));
                    }
                    break;
                case "custom":
                    // TODO custom grouping
                    break;
                default:
                    String message = String.format("The Unsupported type[%s] for Bolt[%s].", type, name);
                    if (logger.isErrorEnabled()) {
                        logger.error(message);
                    }
                    throw new IllegalArgumentException(message);
            }
        }
    }

    private Config createConfig(JSONObject json) {
        Config config = new Config();
        // 设置基本配置信息
        config.setDebug(json.getBoolean("debug") != null ? json.getBoolean("debug") : false);
        config.setMaxSpoutPending(json.getInteger("maxSpoutPending") != null ? json.getInteger("maxSpoutPending") : 3);
        config.setMaxTaskParallelism(json.getInteger("maxTaskParallelism") != null ? json.getInteger("maxTaskParallelism") : 3);
        config.setMessageTimeoutSecs(json.getInteger("messageTimeoutSecs") != null ? json.getInteger("messageTimeoutSecs") : 3);
        config.setNumAckers(json.getInteger("numAckers") != null ? json.getInteger("numAckers") : 3);
        config.setNumWorkers(json.getInteger("numWorkers") != null ? json.getInteger("numWorkers") : 5);
        config.setTopologyPriority(json.getInteger("priority") != null ? json.getInteger("priority") : 3);
        // 设置JDBC数据源
        JSONArray dataSources = json.getJSONArray("dataSources");
        if (dataSources != null) {
            config.put("dataSources", dataSources.toJSONString());
        }
        // 设置数据列和数据列描述
        JSONArray columns = json.getJSONArray("columns");
        if (columns != null) {
            config.put("columns", columns.toJSONString());
        }
        // 设置zookeeper配置
        JSONObject zookeeper = json.getJSONObject("zookeeper");
        if (zookeeper != null) {
            config.put("zookeeper", zookeeper.toJSONString());
        }
        // 设置配置的校验规则和转换规则
        JSONObject validates = json.getJSONObject("validates");
        if (validates != null) {
            config.put("validates", validates.toJSONString());
        }
        JSONObject transforms = json.getJSONObject("transforms");
        if (transforms != null) {
            config.put("transforms", transforms.toJSONString());
        }
        return config;
    }
}
