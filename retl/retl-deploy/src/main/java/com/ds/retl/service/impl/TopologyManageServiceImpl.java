package com.ds.retl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.ds.retl.ETLTopologyBuilder;
import com.ds.retl.cli.RETLStormCli;
import com.ds.retl.dal.entity.Topology;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.OperateLogService;
import com.ds.retl.service.StormRestfulServiceClient;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * 基于Hibernate JPA实现的计算拓扑管理的相关服务实现类
 *
 * @author : john.peng created on date : 2017/10/10
 */
@Component
public class TopologyManageServiceImpl implements TopologyManageService {
    private static final Log logger = LogFactory.getLog(TopologyManageServiceImpl.class);

    @Autowired
    private Environment env = null;

    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralAccessor accessor = null;

    @Autowired
    private OperateLogService operateLogService = null;

    @Autowired
    private StormRestfulServiceClient stormClient = null;

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageService#save(String, String)
     */
    @Transactional
    @Override
    public Topology save(String id, String topologyJsonStr) throws UserInterfaceErrorException {
        try {
            Topology topology;
            if (StringUtils.isBlank(id)) {
                topology = EntityFactory.createEntity(Topology.class);
            } else {
                topology = accessor.getById(id, Topology.class);
                if (topology == null) {
                    throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NOT_FOUND);
                }
            }
            JSONObject json = JSON.parseObject(topologyJsonStr);
            String name = json.getString("name");
            topology.setName(name);
            topology.setDescription(json.getString("description"));
            topology.setSubmitted(false);
            topology.setSubmitInfo("");
            topology.setTopologyId(null);
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

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageServiceImpl#kill(String)
     */
    @Override
    public Topology kill(String id) throws UserInterfaceErrorException {
        try {
            Topology topology = accessor.getById(id, Topology.class);
            if (topology == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NOT_FOUND);
            }
            if (StringUtils.isBlank(topology.getTopologyId()) || !topology.isSubmitted()) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NOT_SUBMITTED);
            }
            JSONObject result = stormClient.getTopology(topology.getTopologyId());
            if (result == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NOT_SUBMITTED);
            }
            result = stormClient.kill(topology.getTopologyId());
            if (result == null || !"success".equals(result.getString("status"))) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_KILL_FAIL);
            }
            topology.setSubmitted(false);
            topology.setSubmitInfo(null);
            topology.setTopologyId(null);
            topology = accessor.save(topology);
            operateLogService.writeLog(String.format("杀死计算拓扑[%s]操作成功。", topology.getName()));
            return topology;
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageService#delete(String)
     */
    @Override
    public void delete(String id) throws UserInterfaceErrorException {
        try {
            Topology topology = accessor.getById(id, Topology.class);
            if (topology == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NOT_FOUND);
            }
            accessor.remove(topology);
            operateLogService.writeLog(String.format("删除计算拓扑[%s]操作成功。", topology.getName()));
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    private JSONObject foundSubmitedTopology(String name) throws UserInterfaceErrorException {
        JSONArray topologies = stormClient.getToptologies();
        if (topologies != null && topologies.size() > 0) {
            for (int index = 0; index < topologies.size(); index++) {
                JSONObject item = topologies.getJSONObject(index);
                if (item != null && name.equals(item.getString("name"))) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 提交一个拓扑实体对象到Storm集群中
     *
     * @param topology   拓扑实体对象
     * @param simulation 是否仿真
     * @return 成功返回拓扑实体对象
     * @throws UserInterfaceErrorException 提交过程中发生的异常
     */
    private Topology submit(Topology topology, boolean simulation) throws UserInterfaceErrorException {
        String topologyName = topology.getName();
        // 检查拓扑名字是否已经被部署到Storm集群中
        JSONObject found = foundSubmitedTopology(topologyName);
        if (found != null) {
            try {
                topology.setSubmitted(false);
                topology.setSubmittedTime(new Date().getTime());
                topology.setSubmitInfo("同名的计算拓扑已经被部署到集群中，不能重复部署。");
                accessor.save(topology);
            } catch (EntityAccessException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Save submit information fail.", ex);
                }
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_ALREADY_SUBMITTED);
        }
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
        if (!simulation) {
            String stormHome = env.getProperty("storm.home"), stormBin = env.getProperty("storm.bin"),
                    retlHome = env.getProperty("storm.retl"), retlPlatform = env.getProperty("storm.retl.platform"),
                    retlDeps = env.getProperty("storm.retl.deps"), retlConf = env.getProperty("storm.retl.conf");
            String configName = String.format("%s/%s/%s.json", retlHome, retlConf, topologyName);
            File file = new File(configName);
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
                bw.write(JSON.toJSONString(topologyJson, true));
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(ex);
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_FILE_OPERATE_FAIL);
            }
            try {
                String submitInfo = new RETLStormCli(stormHome, stormBin, retlHome, retlPlatform, retlDeps)
                        .deploy(configName, 10);
                topology.setSubmitInfo(submitInfo);
                for (int index = 0; index < 10; index++) {
                    JSONObject submittedTopology = foundSubmitedTopology(topologyName);
                    if (submittedTopology != null) {
                        topology.setSubmittedTime(new Date().getTime());
                        String topologyId = submittedTopology.getString("id");
                        topology.setTopologyId(topologyId);
                        topology.setSubmitted(true);
                        topology = accessor.save(topology);
                        operateLogService.writeLog(String.format("成功提交[%s]计算拓扑。", topologyName));
                        return topology;
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            if (logger.isErrorEnabled()) {
                                logger.error(ex);
                            }
                        }
                    }
                }
                if (logger.isWarnEnabled()) {
                    logger.warn("The topology can be found in cluster after wait 10s.");
                }
                topology.setSubmittedTime(new Date().getTime());
                topology.setSubmitted(false);
                accessor.save(topology);
                operateLogService.writeLog(String.format("提交[%s]计算拓扑未成功。", topologyName));
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_SUBMIT_FAIL);
            } catch (EntityAccessException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(ex);
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
            }
        } else {
            // 本地仿真运行
            new ETLTopologyBuilder().buildTopology(false, topologyJson);
            return topology;
        }
    }

    /**
     * 从预定义模版中加载JSON对象
     *
     * @return 成功返回JSON对象，否则返回null。
     */
    private JSONObject loadTemplateJson(String templateFile) {
        try (JSONReader jr = new JSONReader(new InputStreamReader(
                TopologyManageServiceImpl.class.getResourceAsStream(templateFile)))) {
            JSONObject conf = JSON.parseObject(jr.readString());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Load topology template from calsspath:%s success.", templateFile));
            }
            return conf;
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return null;
        }
    }

    /**
     * 从拓扑的配置信息字符串中创建一个JSON格式的拓扑配置对象
     *
     * @param confStr 拓扑配置信息字符串
     * @return 创建好的拓扑配置对象
     * @throws UserInterfaceErrorException 创建过程中发生的异常
     */
    private JSONObject prepareTopologyConfigJsonObject(String confStr) throws UserInterfaceErrorException {
        JSONObject conf = JSON.parseObject(confStr);
        if (conf == null) {
            return null;
        }
        JSONObject topologyConf = loadTemplateJson("/template/topology-template.json");
        if (topologyConf == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The inline template config file not found in system.");
            }
            return null;
        }
        // 基础信息
        topologyConf.put("name", conf.getString("name"));
        topologyConf.put("type", conf.getString("type"));
        topologyConf.put("cluster", true);
        topologyConf.put("debug", conf.getBooleanValue("debug"));
        topologyConf.put("maxSpoutPending", 1);
        //topologyConf.put("maxTaskParallelism", conf.getIntValue("maxTaskParallelism"));
        topologyConf.put("messageTimeoutSecs", conf.getIntValue("messageTimeoutSecs"));
        //topologyConf.put("numAckers", conf.getIntValue("numAckers"));
        topologyConf.put("numAckers", 1);
        //topologyConf.put("numWorkers", conf.getIntValue("numWorkers"));
        // 处理配置的ZOOKEEPER服务器配置
        JSONArray zookeepers = conf.getJSONArray("zookeepers");
        JSONObject zookeeper = new JSONObject();
        StringBuffer serverList = new StringBuffer();
        for (int index = 0; index < zookeepers.size(); index++) {
            serverList.append(zookeepers.getString(index));
            serverList.append(",");
        }
        int length = serverList.length();
        if (serverList.length() > 0) {
            length--;
        }
        zookeeper.put("serverList", serverList.substring(0, length));
        topologyConf.put("zookeepers", zookeeper);

        // 数据源信息
        topologyConf.put("jdbcDataSources", conf.getJSONArray("jdbcDataSources"));
        JSONArray jmsDataSources = conf.getJSONArray("jmsDataSources");

        // 缓存
        JSONArray caches = conf.getJSONArray("caches");
        JSONObject tarCaches = new JSONObject();
        if (caches != null) {
            for (int index = 0; index < caches.size(); index++) {
                JSONObject src = caches.getJSONObject(index);
                JSONObject tar = new JSONObject();
                String columnName = src.getString("columnName"), type = src.getString("type");
                tar.put("type", type);
                if ("STATIC".equals(type)) {
                    String dataEnum = src.getString("dataEnum");
                    tar.put("dataEnum", dataEnum);
                } else if ("JDBC".equals(type)) {
                    String dataSource = src.getString("dataSource");
                    String sql = src.getString("sql");
                    int intervalSec = src.getIntValue("intervalSec");
                    tar.put("dataSource", dataSource);
                    tar.put("sql", sql);
                    tar.put("intervalSec", intervalSec);
                } else {
                    throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
                }
                tarCaches.put(columnName, tar);
            }
        }
        topologyConf.put("caches", tarCaches);

        // spouts信息
        JSONArray spouts = conf.getJSONArray("spouts");
        // 如果指定了JDBC的Spout，那么必须将并行度全部设置为1
        boolean foundJdbcSpout = false;
        int defaultParallelism = 0;
        for (int index = 0; index < spouts.size(); index++) {
            JSONObject spout = spouts.getJSONObject(index);
            String type = spout.getString("type");
            int parallelism = spout.getIntValue("parallelism");
            defaultParallelism = Math.max(defaultParallelism, parallelism);
            if ("jdbc".equalsIgnoreCase(type)) {
                spout.put("parallelism", 1);
                foundJdbcSpout = true;
                break;
            }
        }
        if (defaultParallelism <= 0) {
            defaultParallelism = 1;
        }
        if (foundJdbcSpout) {
            if (spouts.size() != 1) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_CONF_JDBC_SPOUT);
            }
            // 发现配置的是JDBC类型的采集源，则只能有一个采集源，并且并行度必须为1
            topologyConf.put("maxTaskParallelism", 1);
            topologyConf.put("numWorkers", 1);
        } else {
            topologyConf.put("maxTaskParallelism", defaultParallelism);
            topologyConf.put("numWorkers", defaultParallelism);
        }
        spouts = prepareSpouts(spouts, jmsDataSources);
        topologyConf.put("spouts", spouts);

        // bolts信息
        JSONArray bolts = new JSONArray();
        String type = conf.getString("type");
        if ("retl".equals(type)) {
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
            setGroups(errorBolt, Arrays.asList(structureBolt, validateBolt, transformBolt), true);
            bolts.addAll(Arrays.asList(structureBolt, validateBolt, transformBolt, errorBolt));
            // 根据配置的JMS存储目标名称个数，初始化相应的JMS存储Bolt
            List<String> jmsTars = conf.getJSONArray("tarDestinateNames").toJavaList(String.class);
            for (String tarDestinateName : jmsTars) {
                JSONObject jmsBolt = createBolt(String.format("jms-bolt-%s", tarDestinateName), "JMS",
                        foundJdbcSpout ? 1 : defaultParallelism);
                JSONObject jmsBoltConfig = new JSONObject();
                JSONObject jmsDataSource = conf.getJSONArray("jmsDataSources").getJSONObject(0);
                jmsBolt.put("method", jmsDataSource.getString("method"));
                jmsBoltConfig.put("connection", String.format("%s%s?trace=%s", jmsDataSource.getString("protocol"),
                        jmsDataSource.getString("server"),
                        jmsDataSource.getBooleanValue("trace") ? "true" : "false"));
                jmsBoltConfig.put("user", jmsDataSource.getString("user"));
                jmsBoltConfig.put("password", jmsDataSource.getString("password"));
                jmsBoltConfig.put("destinateName", tarDestinateName);
                jmsBoltConfig.put("isTopic", conf.getBooleanValue("tarIsTopic"));
                jmsBolt.put("configuration", jmsBoltConfig);
                setGroups(jmsBolt, Arrays.asList(transformBolt, errorBolt));
                bolts.add(jmsBolt);
            }
        } else if ("persist".equals(type)) {
            // persist信息
            JSONObject persist = conf.getJSONObject("persist");
            if (persist == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NO_CONF_PERSIST);
            }
            JSONObject persistBolt = createBolt("persist-bolt", "JDBC", defaultParallelism);
            persistBolt.put("configuration", persist);
            setGroups(persistBolt, spouts.toJavaList(JSONObject.class));
            bolts.add(persistBolt);
            // 为关联的JDBC数据源添加错误持久化配置信息
            String dataSourceName = persist.getString("dataSource");
            JSONArray jdbcDataSources = topologyConf.getJSONArray("jdbcDataSources");
            JSONObject errorTable = this.loadTemplateJson("/template/error-template.json");
            for (int index = 0; index < jdbcDataSources.size(); index++) {
                JSONObject dataSource = jdbcDataSources.getJSONObject(index);
                if (dataSourceName.equals(dataSource.getString("name"))) {
                    dataSource.put("errorTable", errorTable);
                }
            }
            topologyConf.put("jdbcDataSources", jdbcDataSources);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Unsupported topology type[%s].", type));
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        topologyConf.put("bolts", bolts);

        // columns信息
        JSONArray columns = conf.getJSONArray("columns");
        if (columns != null) {
            topologyConf.put("columns", columns);
        }

        // validates信息
        JSONObject validates = conf.getJSONObject("validates");
        if (validates != null) {
            topologyConf.put("validates", validates);
        }

        // transforms信息
        JSONObject transforms = conf.getJSONObject("transforms");
        if (transforms != null) {
            topologyConf.put("transforms", transforms);
        }

        return topologyConf;
    }

    private JSONArray prepareSpouts(JSONArray spouts, JSONArray jmsDataSources) {
        JSONArray result = new JSONArray();
        for (int index = 0; index < spouts.size(); index++) {
            JSONObject spout = spouts.getJSONObject(index);
            if (spout != null) {
                JSONObject tar = new JSONObject();
                tar.put("name", spout.getString("name"));
                String type = spout.getString("type");
                tar.put("type", type);
                int parallelism = spout.getIntValue("parallelism");
                tar.put("parallelism", parallelism);
                if ("jdbc".equalsIgnoreCase(type)) {
                    prepareJdbcSpout(spout, tar);
                } else if ("jms".equalsIgnoreCase(type) || "jmsPull".equalsIgnoreCase(type)) {
                    prepareJmsSpout(spout, tar, jmsDataSources);
                }
                result.add(tar);
            }
        }
        return result;
    }

    private void prepareJdbcSpout(JSONObject src, JSONObject tar) {
        JSONObject srcConf = src.getJSONObject("configuration");
        JSONObject tarConf = new JSONObject();
        tarConf.put("dataSource", srcConf.getString("dataSource"));
        tarConf.put("table", srcConf.getString("table"));
        tarConf.put("key", srcConf.getString("key"));
        tarConf.put("timestamp", srcConf.getString("timestamp"));
        tarConf.put("ackPath", srcConf.getString("ackPath"));
        tarConf.put("windowSize", srcConf.getIntValue("windowSize"));
        tarConf.put("intervalSec", srcConf.getIntValue("intervalSecs"));
        tarConf.put("fields", StringUtils.merge(srcConf.getJSONArray("fields").toArray(new String[0])));
        tarConf.put("fieldTransform",
                new JSONObject(transformFieldMapping(srcConf.getJSONArray("fieldsTransform"))));
        tar.put("configuration", tarConf);
    }

    private Map<String, Object> transformFieldMapping(JSONArray rows) {
        Map<String, Object> mapping = new HashMap<>();
        for (int index = 0; index < rows.size(); index++) {
            String row = rows.getString(index);
            String[] sides = row.split("=>");
            if (sides.length == 2) {
                mapping.put(sides[0], sides[1]);
            }
        }
        return mapping;
    }

    private void prepareJmsSpout(JSONObject src, JSONObject tar, JSONArray jmsDataSources) {
        JSONObject srcConf = src.getJSONObject("configuration");
        String dataSource = srcConf.getString("dataSource");
        JSONObject jmsDataSource = findDataSource(dataSource, jmsDataSources);
        if (jmsDataSource == null) {
            throw new IllegalArgumentException(String.format("The Jms DataSource[%s] not found.", dataSource));
        }
        tar.put("method", jmsDataSource.getString("method"));
        JSONObject tarConf = new JSONObject();
        tarConf.put("connection", String.format("%s%s?trace=%s", jmsDataSource.getString("protocol"),
                jmsDataSource.getString("server"),
                jmsDataSource.getBooleanValue("trace") ? "true" : "false"));
        tarConf.put("user", jmsDataSource.getString("user"));
        tarConf.put("password", jmsDataSource.getString("password"));
        tarConf.put("destinateName", srcConf.getString("destinateName"));
        tarConf.put("isTopic", srcConf.getBooleanValue("isTopic"));
        tarConf.put("producer", srcConf.getString("producer"));
        tarConf.put("fields", StringUtils.merge(srcConf.getJSONArray("fields").toArray(new String[0])));
        tarConf.put("fieldTransform",
                new JSONObject(transformFieldMapping(srcConf.getJSONArray("fieldsTransform"))));
        tar.put("configuration", tarConf);
    }

    private JSONObject findDataSource(String name, JSONArray dataSources) {
        for (int index = 0; index < dataSources.size(); index++) {
            JSONObject dataSource = dataSources.getJSONObject(index);
            if (name.equals(dataSource.getString("name"))) {
                return dataSource;
            }
        }
        return null;
    }

    /**
     * 创建一个Bolt对象
     *
     * @param name        名称
     * @param type        类型
     * @param parallelism 并行度
     * @return 创建好的Bolt对象
     */
    private JSONObject createBolt(String name, String type, int parallelism) {
        JSONObject bolt = new JSONObject();
        bolt.put("name", name);
        bolt.put("type", type);
        bolt.put("parallelism", parallelism);
        return bolt;
    }

    /**
     * 设置Bolt分发组信息
     *
     * @param bolt  Bolt对象
     * @param prevs 前置环境列表
     */
    private void setGroups(JSONObject bolt, List<JSONObject> prevs) {
        setGroups(bolt, prevs, false);
    }

    /**
     * 设置Bolt分发组信息
     *
     * @param bolt        Bolt对象
     * @param prevs       前置环境列表
     * @param isErrorBolt 如果设置为true，表示本Bolt是一个错误处理的Bolt；否则是一个常规的Bolt
     */
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

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageService#submit(String, boolean)
     */
    @Transactional
    @Override
    public Topology submit(String id, boolean simulation) throws UserInterfaceErrorException {
        try {
            Topology topology = accessor.getById(id, Topology.class);
            if (topology == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_NOT_FOUND);
            }
            return submit(topology, simulation);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageService#submit(String, String)
     */
    @Transactional
    @Override
    public Topology submit(String id, String topologyJsonStr) throws UserInterfaceErrorException {
        Topology topology = save(id, topologyJsonStr);
        return submit(topology, false);
    }

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageService#validateZookeepers(String)
     */
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

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageService#validateJdbcDataSource(String)
     */
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

    /**
     * {@inheritDoc}
     *
     * @see TopologyManageService#validateJmsDataSource(String)
     */
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
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("validate JMS connection: %s.", connStr));
                }
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
