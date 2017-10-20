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
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;
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
 *
 * @author : john.peng created on date : 2017/9/7
 */
public class ETLTopologyBuilder {
    private static final Log logger = LogFactory.getLog(ETLTopologyBuilder.class);

    /**
     * 实时ETL发布的入口函数
     *
     * @param args 部署参数
     */
    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            printUsage();
            return;
        }
        System.out.println("******************************************************");
        System.out.println("开始提交[实时数据ETL]拓扑到集群中......");
        String configFile = args[0];
        try (JSONReader reader = new JSONReader(new InputStreamReader(new FileInputStream(configFile)))) {
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

    /**
     * 打印命令使用方法
     */
    private static void printUsage() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                ETLTopologyBuilder.class.getResourceAsStream("/readme.txt")))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 建立一个实时ETL计算拓扑
     *
     * @param isCluster 如果设置为true，表示提交到Storm集群中；否则提交到本地虚拟集群中进行测试验证。
     * @param json      拓扑的配置信息
     */
    public void buildTopology(boolean isCluster, JSONObject json) {
        Config config = createConfig(json);

        TopologyBuilder builder = new TopologyBuilder();
        String topologyName = json.getString("name");
        String topologyType = json.getString("type");
        if (StringUtils.isBlank(topologyName)) {
            String message = "Not define the name for the topology.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw new IllegalArgumentException(message);
        }

        // 创建定义的Spout
        JSONArray spoutArray = json.getJSONArray("spouts");
        createSpouts(spoutArray, "persist".equals(topologyType), builder);

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

    /**
     * 建立拓扑中的数据采集器
     *
     * @param spoutArray 采集器配置信息
     * @param builder    拓扑创建者对象
     */
    private void createSpouts(JSONArray spoutArray, boolean isPersist, TopologyBuilder builder) {
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
            if ("jms".equalsIgnoreCase(type) || "jmsPULL".equalsIgnoreCase(type)) {
                // JMS Spout
                String method = jsonSpout.getString("method");
                JmsManager.Supported supported = JmsManager.Supported.valueOf(method);
                JSONObject configuration = jsonSpout.getJSONObject("configuration");
                try {
                    BaseRichSpout spout = "JMS".equalsIgnoreCase(type) ?
                            JmsManager.createJmsSpout(supported, configuration, isPersist) :
                            JmsManager.createJmsPullSpout(supported, configuration, isPersist);
                    builder.setSpout(name, spout, parallelism);
                } catch (JMSException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Create JmsSpout fail, name: %s, method: %s, configuration: %s.",
                                name, method, configuration.toJSONString()), ex);
                    }
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                }
            } else if ("jdbc".equalsIgnoreCase(type)) {
                JSONObject configuration = jsonSpout.getJSONObject("configuration");
                JdbcSpout spout = new JdbcSpout(configuration);
                // JdbcSpout的并行度必须设置为1，防止数据库死锁
                builder.setSpout(name, spout, 1);
            } else {
                throw new IllegalArgumentException(String.format("Unsupported spout's type: %s.", type));
            }
        }
    }

    /**
     * 创建拓扑中的处理Bolt单元
     *
     * @param boltArray Bolt配置信息
     * @param builder   拓扑创建者对象
     */
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

    /**
     * 设置Bolt的元组分发策略
     *
     * @param name       发送者名称
     * @param bd         Bolt定义
     * @param jsonGroups 分发配置信息
     */
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

    /**
     * 创建tuop传递的配置信息
     *
     * @param json 配置信息
     * @return 返回配置信息对象，最终在Storm集群中的各个拓扑任务直接传递。
     */
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
        // 设置数据源
        JSONArray jdbcDataSources = json.getJSONArray("jdbcDataSources");
        if (jdbcDataSources != null) {
            config.put("jdbcDataSources", jdbcDataSources.toJSONString());
        }
        // 设置缓存
        JSONObject caches = json.getJSONObject("caches");
        if (caches != null) {
            config.put("caches", caches.toJSONString());
        }
        // 设置数据列和数据列描述
        JSONArray columns = json.getJSONArray("columns");
        if (columns != null) {
            config.put("columns", columns.toJSONString());
        }
        // 设置zookeeper配置
        JSONObject zookeepers = json.getJSONObject("zookeepers");
        if (zookeepers != null) {
            config.put("zookeepers", zookeepers.toJSONString());
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
