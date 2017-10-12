package com.ds.retl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.ds.retl.cli.RETLStormCli;
import com.ds.retl.dal.entity.Topology;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.OperateLogService;
import com.ds.retl.service.TopologyManageService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by john on 2017/10/10.
 */
@Component
public class TopologyManageServiceImpl implements TopologyManageService {
    private static final Log logger = LogFactory.getLog(TopologyManageServiceImpl.class);

    private static final String confPath = "/opt/storm/retl/config";

    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralAccessor accessor = null;

    @Autowired
    private OperateLogService operateLogService = null;

    @Override
    public Topology save(String name, String topologyJsonStr) throws UserInterfaceErrorException {
        try {
            Topology topology = EntityFactory.createEntity(Topology.class);
            topology.setName(name);
            topology.setSubmitted(false);
            topology.setSubmitInfo("");
            topology.setTopologyContent(topologyJsonStr);
            topology = accessor.save(topology);
            operateLogService.writeLog(String.format("成功保存[%s]计算拓扑。", name));
            return topology;
        } catch (EntityAccessException | EntityInstantiationException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    private Topology submit(Topology topology) throws UserInterfaceErrorException {
        String topologyName = topology.getName();
        String confStr = topology.getTopologyContent();
        if (StringUtils.isBlank(confStr)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Topology's content is blank, name: %s.", topologyName));
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        JSONObject topologyJson = prepareTopologyConfigJsonObject(confStr);
        if (topology == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Parse JSON object fail, conf: %s.", confStr));
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String configName = String.format("%s/%s.json", confPath, topologyName);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configName)))) {
            bw.write(topologyJson.toJSONString());
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_FILE_OPERATE_FAIL);
        }
        try {
            String submitInfo = new RETLStormCli().deploy(configName);
            topology.setSubmitInfo(submitInfo);
            topology.setSubmitted(true);
            topology.setSubmittedTime(new Date().getTime());
            topology = accessor.save(topology);
            operateLogService.writeLog(String.format("成功提交[%s]计算拓扑。", topologyName));
            return topology;
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    private JSONObject loadTemplateTopology() {
        try (JSONReader jr = new JSONReader(new InputStreamReader(
                TopologyManageServiceImpl.class.getResourceAsStream("/template/topology-template.json")))) {
            JSONObject conf = JSON.parseObject(jr.readString());
            if (logger.isDebugEnabled()) {
                logger.debug("Load topology template from calsspath:/template/topology-template.json success.");
            }
            return conf;
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return null;
        }
    }

    private JSONObject prepareTopologyConfigJsonObject(String confStr) throws UserInterfaceErrorException {
        JSONObject conf = JSON.parseObject(confStr);
        if (conf == null) {
            return null;
        }
        JSONObject topologyConf = loadTemplateTopology();
        if (topologyConf == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The inline template config file not found in system.");
            }
            return null;
        }
        // 基础信息
        topologyConf.put("name", conf.getString("name"));
        topologyConf.put("cluster", true);
        topologyConf.put("debug", conf.getBooleanValue("debug"));
        topologyConf.put("maxSpoutPending", 1);
        topologyConf.put("maxTaskParallelism", conf.getIntValue("maxTaskParallelism"));
        topologyConf.put("messageTimeoutSecs", conf.getIntValue("messageTimeoutSecs"));
        topologyConf.put("numAckers", conf.getIntValue("numAckers"));
        topologyConf.put("numWorkers", conf.getIntValue("numWorkers"));
        topologyConf.put("zookeepers", conf.getJSONArray("zookeepers"));

        // 数据源信息
        topologyConf.put("jdbcDataSources", conf.getJSONArray("jdbcDataSources"));
        topologyConf.put("jmsDataSources", conf.getJSONArray("jmsDataSources"));

        // spouts信息
        JSONArray spouts = conf.getJSONArray("spouts");
        // 如果指定了JDBC的Spout，那么必须将并行度全部设置为1
        boolean foundJdbcSpout = false;
        for (int index = 0; index < spouts.size(); index++) {
            JSONObject spout = spouts.getJSONObject(index);
            String type = spout.getString("type");
            if ("jdbc".equalsIgnoreCase(type)) {
                spout.put("parallelism", 1);
                foundJdbcSpout = true;
                break;
            }
        }
        if (foundJdbcSpout) {
            if (spouts.size() != 1) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_CONF_JDBC_SPOUT);
            }
            // 发现配置的是JDBC类型的采集源，则只能有一个采集源，并且并行度必须为1
            topologyConf.put("maxTaskParallelism", 1);
            topologyConf.put("numAckers", 1);
            topologyConf.put("numWorkers", 1);
        }
        topologyConf.put("spouts", spouts);

        // bolts信息
        int defaultParallelism = topologyConf.getIntValue("maxTaskParallelism");
        JSONArray bolts = new JSONArray();
        JSONObject structureBolt = createBolt("structure-bolt", "STRUCTURE",
                foundJdbcSpout ? 1 : defaultParallelism);
        setGroups(structureBolt, spouts.toJavaList(JSONObject.class));
        JSONObject validateBolt = createBolt("validate-bolt", "VALIDATE",
                foundJdbcSpout ? 1 : defaultParallelism);
        setGroups(validateBolt, Arrays.asList(structureBolt));
        JSONObject transformBolt = createBolt("transform-bolt", "TRANSFORM",
                foundJdbcSpout ? 1 : defaultParallelism);
        setGroups(transformBolt, Arrays.asList(validateBolt));
        JSONObject errorBolt = createBolt("error-bolt", "ERROR", foundJdbcSpout ? 1 : defaultParallelism);
        setGroups(errorBolt, Arrays.asList(structureBolt, validateBolt, transformBolt));
        JSONObject jmsBolt = createBolt("jms-bolt", "JMS", foundJdbcSpout ? 1 : defaultParallelism);
        JSONObject jmsBoltConfig = new JSONObject();
        jmsBoltConfig.put("dataSource", conf.getJSONArray("jmsDataSources").getJSONObject(0)
                .getString("name"));
        jmsBoltConfig.put("destinateName", conf.getString("tarDestinateName"));
        jmsBoltConfig.put("isTopic", conf.getBooleanValue("tarIsTopic"));
        jmsBolt.put("configuration", jmsBoltConfig);
        setGroups(jmsBolt, Arrays.asList(transformBolt, errorBolt));
        bolts.addAll(Arrays.asList(structureBolt, validateBolt, transformBolt, errorBolt, jmsBolt));
        topologyConf.put("bolts", bolts);

        // columns信息
        topologyConf.put("columns", conf.getJSONArray("columns"));

        // validates信息
        topologyConf.put("validates", conf.getJSONObject("validates"));

        // transforms信息
        topologyConf.put("transforms", conf.getJSONObject("transforms"));

        return topologyConf;
    }

    private JSONObject createBolt(String name, String type, int parallelism) {
        JSONObject bolt = new JSONObject();
        bolt.put("name", name);
        bolt.put("type", type);
        bolt.put("parallelism", parallelism);
        return bolt;
    }

    private void setGroups(JSONObject bolt, List<JSONObject> prevs) {
        setGroups(bolt, prevs, false);
    }

    private void setGroups(JSONObject bolt, List<JSONObject> prevs, boolean isErrorBolt) {
        JSONArray groups = new JSONArray();
        prevs.forEach(prev -> {
            JSONObject group = new JSONObject();
            group.put("type", "shuffle");
            group.put("source", prev.getString("name"));
            if (isErrorBolt) {
                group.put("streamId", "error-stream");
            }
            groups.add(group);
        });
        bolt.put("groups", groups);
    }

    @Override
    public Topology submit(String id) throws UserInterfaceErrorException {
        try {
            Topology topology = accessor.getById(id, Topology.class);
            if (topology == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NOT_FOUND);
            }
            return submit(topology);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    @Override
    public Topology submit(String name, String topologyJsonStr) throws UserInterfaceErrorException {
        Topology topology = save(name, topologyJsonStr);
        return submit(topology);
    }

    @Override
    public boolean validateZookeepers(String resourceJsonStr) throws UserInterfaceErrorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Start validate the Zookeeper server ...");
        }
        List<String> servers = JSON.parseObject(resourceJsonStr, List.class);
        try {
            ZooKeeper zk = new ZooKeeper(StringUtils.merge(servers, ","), 40 * 1000, null);
            zk.getState();
            zk.close();
            if (logger.isDebugEnabled()) {
                logger.debug("Validate the Zookeeper server successfully.");
            }
            return true;
        } catch (IOException | InterruptedException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_VALIDATE_FAIL);
        }
    }

    @Override
    public boolean validateJdbcDataSource(String resourceJsonStr) throws UserInterfaceErrorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Start validate the JDBC server ...");
        }
        JSONObject json = JSON.parseObject(resourceJsonStr);
        String driver = json.getString("driver"),
                url = json.getString("url"),
                user = json.getString("user"),
                password = json.getString("password");
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.isClosed();
            conn.close();
            if (logger.isDebugEnabled()) {
                logger.debug("Validate the JDBC server successfully.");
            }
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_VALIDATE_FAIL);
        }
    }

    @Override
    public boolean validateJmsDataSource(String resourceJsonStr) throws UserInterfaceErrorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Start validate the JMS server ...");
        }
        JSONObject json = JSON.parseObject(resourceJsonStr);
        String protocol = json.getString("protocol"),
                server = json.getString("server"),
                user = json.getString("user"),
                password = json.getString("password"),
                method = json.getString("method");
        boolean trace = json.getBooleanValue("trace");
        String connStr = String.format("%s%s?trace=%s", protocol, server, trace ? "true" : "false");
        if ("ACTIVEMQ".equals(method)) {
            try {
                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, connStr);
                javax.jms.Connection conn = factory.createConnection();
                conn.getClientID();
                conn.close();
                if (logger.isDebugEnabled()) {
                    logger.debug("Validate the JMS server successfully.");
                }
                return true;
            } catch (JMSException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(ex);
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_VALIDATE_FAIL);
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Not supported type: %s.", method));
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
    }
}
