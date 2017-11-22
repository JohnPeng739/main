package com.ds.retl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.cli.SystemCtlCli;
import com.ds.retl.dal.entity.ConfigJson;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.ServerManageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.rest.client.RestClientInvoke;
import org.mx.rest.client.RestInvokeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 2017/11/2.
 */
@Component
public class ServerManageServiceImpl implements ServerManageService {
    private static final Log logger = LogFactory.getLog(ServerManageServiceImpl.class);
    private static final String RETL_SERVERS_PREFIX = "retl.servers";

    @Autowired
    @Qualifier("generalDictEntityAccessorHibernate")
    private GeneralDictAccessor accessor = null;
    @Autowired
    private OperateLogService operateLogService = null;
    @Autowired
    private Environment env = null;

    private String createMachineConfigField(String machineName) {
        String field = String.format("%s.%s", RETL_SERVERS_PREFIX, machineName);
        if (field.length() >= 30) {
            field = field.substring(0, 30);
        }
        return field;
    }

    private JSONObject parseJson(String json) throws UserInterfaceErrorException {
        try {
            return JSON.parseObject(json);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Parse json fail.", json), ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#getServerInfo(String)
     */
    @Override
    public JSONObject getServerInfo(String machineName) throws UserInterfaceErrorException {
        try {
            ConfigJson config = accessor.getByCode(createMachineConfigField(machineName), ConfigJson.class);
            if (config == null || StringUtils.isBlank(config.getConfigContent())) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_CONFIG_NOT_FOUND);
            } else {
                return parseJson(config.getConfigContent());
            }
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    private void prepareZookeeperService(JSONObject config) throws UserInterfaceErrorException {
        if (config == null) {
            return;
        }
        //cluster: false, serverNo: '1', dataDir: '/opt/zookeeper/data', servers: []
        boolean cluster = config.getBooleanValue("cluster");
        int serverNo = config.getInteger("serverNo");
        String dataDir = config.getString("dataDir");
        List<String> servers = config.getObject("servers", List.class);

        if (StringUtils.isBlank(dataDir)) {
            if (logger.isErrorEnabled()) {
                logger.error("The storm data directory not configured.");
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }

        // 目录准备
        new File("/opt/zookeeper/conf").mkdirs();
        new File(dataDir).mkdirs();
        // 写入配置文件
        String zooCfgName = "/opt/zookeeper/conf/zoo.cfg";
        File zooCfg = new File(zooCfgName);
        if (zooCfg.exists()) {
            zooCfg.renameTo(new File(String.format("%s.%d", zooCfgName, new Date().getTime())));
        }
        try (PrintStream ps = new PrintStream(new FileOutputStream(zooCfg))) {
            ps.println("tickTime=2000");
            ps.println();
            ps.println("initLimit=5");
            ps.println();
            ps.println("syncLimit=2");
            ps.println();
            ps.println(String.format("dataDir=%s", dataDir));
            ps.println();
            ps.println("clientPort=2181");
            ps.println();
            if (cluster) {
                // 如果是集群，则写入集群列表
                for (int index = 0; index < servers.size(); index++) {
                    ps.println(String.format("server.%d=%s", index + 1, servers.get(index)));
                }
                ps.println();
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Prepare %s fail.", zooCfgName), ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_ZK_ZOOCFG_FAIL);
        }
        if (cluster) {
            // 写入本机myid文件
            String myidName = String.format("%s/myid", dataDir);
            File myid = new File(myidName);
            try (PrintStream ps = new PrintStream(new FileOutputStream(myid))) {
                ps.println(serverNo);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Prepare %s fail.", myidName), ex);
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_ZK_ZOOCFG_FAIL);
            }
        }
        // 写入zookeeper service文件
        String zookeeperService = "/usr/lib/systemd/system/zookeeper.service";
        try (PrintStream ps = new PrintStream(new FileOutputStream(zookeeperService))) {
            ps.println("[Unit]");
            ps.println("Description=Apache zookeeper service");
            ps.println("After=network.target remote-fs.target nss-lookup.target");
            ps.println();
            ps.println("[Service]");
            ps.println("Type=forking");
            ps.println("ExecStart=/opt/zookeeper/bin/zookeeperd.sh start");
            ps.println("ExecStop=/opt/zookeeper/bin/zookeeperd.sh stop");
            ps.println("ExecReload=/opt/zookeeper/bin/zookeeperd.sh restart");
            ps.println();
            ps.println("[Install]");
            ps.println("WantedBy=multi-user.target");
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Prepare %s fail.", zookeeperService), ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_ZK_SERVICE_FAIL);
        }
        try {
            operateLogService.writeLog("写入ZOOKEEPER服务配置信息成功。");
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    private void prepareStormService(JSONObject config) throws UserInterfaceErrorException {
        if (config == null) {
            return;
        }
        //services: [], zookeepers: [], nimbuses: [], dataDir: '/opt/storm/data', slots: 10, startPort: 6700
        List<String> services = config.getObject("services", List.class);
        List<String> zookeepers = config.getObject("zookeepers", List.class);
        List<String> nimbuses = config.getObject("nimbuses", List.class);
        String dataDir = config.getString("dataDir");
        int slots = config.getIntValue("slots");
        int startPort = config.getIntValue("startPort");

        if (zookeepers == null || zookeepers.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("The zookeeper list not configured.");
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }

        if (nimbuses == null || nimbuses.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("The nimbus feed list not configured.");
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }

        if (StringUtils.isBlank(dataDir)) {
            if (logger.isErrorEnabled()) {
                logger.error("The storm data directory not configured.");
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }

        // 目录准备
        new File("/opt/storm/conf").mkdirs();
        new File("/opt/storm/data").mkdirs();
        new File("/opt/storm/logs").mkdirs();
        // 准备storm配置文件
        String stormYamlName = "/opt/storm/conf/storm.yaml";
        File stormYaml = new File(stormYamlName);
        if (stormYaml.exists()) {
            stormYaml.renameTo(new File(String.format("%s.%d", stormYamlName, new Date().getTime())));
        }
        try (PrintStream ps = new PrintStream(new FileOutputStream(stormYaml))) {
            ps.println("storm.zookeeper.servers:");
            zookeepers.forEach(server -> ps.println(String.format("  - \"%s\"", server)));
            ps.println();
            for (int index = 0; index < nimbuses.size(); index++) {
                nimbuses.set(index, String.format("\"%s\"", nimbuses.get(index)));
            }
            ps.println(String.format("nimbus.seeds: [%s]", StringUtils.merge(nimbuses, ", ")));
            ps.println();
            ps.println(String.format("storm.local.dir: \"%s\"", dataDir));
            ps.println();
            ps.println("supervisor.slots.ports:");
            for (int index = 0; index < slots; index++) {
                ps.println(String.format("  - %d", startPort + index));
            }
            ps.println();
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Prepare %s fail."), ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_STORM_ZOOCFG_FAIL);
        }
        if (services != null && services.size() > 0) {
            // 准备服务列表
            String servicesConfName = "/opt/storm/conf/services.conf";
            File serviceConf = new File(servicesConfName);
            if (serviceConf.exists()) {
                serviceConf.renameTo(new File(String.format("%s.%d", servicesConfName, new Date().getTime())));
            }
            try (PrintStream ps = new PrintStream(new FileOutputStream(serviceConf))) {
                ps.println(StringUtils.merge(services, " "));
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Prepare %s fail.", servicesConfName), ex);
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_STORM_ZOOCFG_FAIL);
            }
        }
        // storm service文件
        String stormService = "/usr/lib/systemd/system/storm.service";
        try (PrintStream ps = new PrintStream(new FileOutputStream(stormService))) {
            ps.println("[Unit]");
            ps.println("Description=Apache storm service");
            ps.println("After=zookeeper.target");
            ps.println();
            ps.println("[Service]");
            ps.println("Type=forking");
            ps.println("ExecStart=/opt/storm/bin/start-server.sh");
            ps.println("ExecStop=/opt/storm/bin/stop-server.sh");
            ps.println();
            ps.println("[Install]");
            ps.println("WantedBy=multi-user.target");
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Prepare %s fail.", stormService), ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_STORM_SERVICE_FAIL);
        }
        try {
            operateLogService.writeLog("写入STORM服务配置信息成功。");
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
     * @see ServerManageService#saveServerInfo(String)
     */
    @Override
    public JSONObject saveServerInfo(String info) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(info)) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        JSONObject json;
        try {
            json = JSON.parseObject(info);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Parse json fail, %s.", info), ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        String machineName = json.getString("machineName"), machineIp = json.getString("machineIp");
        if (StringUtils.isBlank(machineName)) {
            if (logger.isErrorEnabled()) {
                logger.error("The machine's name is blank.");
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            ConfigJson config = accessor.getByCode(createMachineConfigField(machineName), ConfigJson.class);
            if (config == null) {
                config = EntityFactory.createEntity(ConfigJson.class);
                config.setCode(createMachineConfigField(machineName));
            }
            config.setConfigContent(info);
            accessor.save(config);
            config = accessor.getByCode(createMachineConfigField(machineName), ConfigJson.class);
            if (config == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_CONFIG_NOT_FOUND);
            }
            operateLogService.writeLog(String.format("保存服务器[name=%s, ip=%s]配置信息成功。", machineName, machineIp));
            return JSON.parseObject(config.getConfigContent());
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        } catch (EntityInstantiationException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_ENTITY_INSTANCE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#deleteServerInfo(String)
     */
    @Override
    public JSONObject deleteServerInfo(String machineName) throws UserInterfaceErrorException {
        try {
            ConfigJson config = accessor.getByCode(createMachineConfigField(machineName), ConfigJson.class);
            if (config == null) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_CONFIG_NOT_FOUND);
            } else {
                JSONObject json = parseJson(config.getConfigContent());
                String machineIp = json.getString("machineIp");
                accessor.remove(config);
                operateLogService.writeLog(String.format("删除服务器[name=%s, ip=%s]成功。", machineName, machineIp));
                return json;
            }
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    private Map<String, JSONObject> dowithServerInfos(List<ConfigJson> list) {
        Map<String, JSONObject> map = new HashMap<>();
        if (list != null && list.size() > 0) {
            list.forEach(config -> {
                if (config.getCode().startsWith(RETL_SERVERS_PREFIX)) {
                    String info = config.getConfigContent();
                    try {
                        JSONObject json = JSON.parseObject(info);
                        String machineName = json.getString("machineName"), machineIp = json.getString("machineIp");
                        // 获取状态
                        JSONObject status = serviceStatusRestJson(machineIp);
                        if (status != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(String.format("Get %s status success: %s.", machineIp, status.toJSONString()));
                            }
                            json.put("status", status);
                        }
                        map.put(machineName, json);
                    } catch (Exception ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Parse json fail, %s.", info), ex);
                        }
                    }
                }
            });
        }
        return map;
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#getServerInfos()
     */
    @Override
    public Map<String, JSONObject> getServerInfos() throws UserInterfaceErrorException {
        try {
            List<ConfigJson> list = accessor.list(ConfigJson.class);
            return dowithServerInfos(list);
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#getServerInfos(Pagination)
     */
    @Override
    public Map<String, JSONObject> getServerInfos(Pagination pagination) throws UserInterfaceErrorException {
        try {
            List<ConfigJson> list = accessor.list(pagination, ConfigJson.class);
            return dowithServerInfos(list);
        } catch (EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    private String systemctl(String cmd, String service) throws UserInterfaceErrorException {
        switch (cmd) {
            case "enable":
                return SystemCtlCli.enable(service);
            case "disable":
                return SystemCtlCli.disable(service);
            case "start":
                return SystemCtlCli.start(service);
            case "stop":
                return SystemCtlCli.stop(service);
            case "restart":
                return SystemCtlCli.restart(service);
            case "reload":
                return SystemCtlCli.reload(service);
            case "status":
                return SystemCtlCli.status(service);
            case "is-enabled":
                return SystemCtlCli.isEnabled(service);
            case "is-active":
                return SystemCtlCli.isActive(service);
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported systemctl cmd type: %s.", cmd));
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_UNSUPPORTED_OPERATE);
        }
    }

    private JSONObject getServiceConfig(String machineIp, String service) throws UserInterfaceErrorException {
        Map<String, JSONObject> serverInfos = getServerInfos();
        if (serverInfos != null && !serverInfos.isEmpty()) {
            for (JSONObject row : serverInfos.values()) {
                if (machineIp.equals(row.getString("machineIp"))) {
                    JSONObject config = row.getJSONObject(service);
                    if (config != null) {
                        return config;
                    }
                }
            }
        }
        throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_NOT_CONFIGURE);
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#serviceRest(String, String, String)
     */
    @Override
    public boolean serviceRest(String cmd, String service, String machineIp) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(cmd) || StringUtils.isBlank(service) || StringUtils.isBlank(machineIp)) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        RestClientInvoke invoke = new RestClientInvoke();
        try {
            JSONObject serviceConfig = getServiceConfig(machineIp, service);
            int restPort = env.getProperty("restful.port", Integer.class, 9999);
            JSONObject json = invoke.post(String.format("http://%s:%d/rest/server/service/local?cmd=%s&service=%s",
                    machineIp, restPort, cmd, service), serviceConfig,
                    JSONObject.class);
            return json.getBooleanValue("data");
        } catch (RestInvokeException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_STATUS_FAIL);
        } finally {
            invoke.close();
        }
    }

    private String serviceLocal(ServiceType type, String cmd, String service) throws UserInterfaceErrorException {
        return serviceLocal(type, cmd, service, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#serviceLocal(ServiceType, String, String, JSONObject)
     */
    @Override
    public String serviceLocal(ServiceType type, String cmd, String service, JSONObject serviceConfig)
            throws UserInterfaceErrorException {
        if (serviceConfig != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("service config: %s.", serviceConfig.toJSONString()));
            }
            if ("zookeeper".equals(service)) {
                prepareZookeeperService(serviceConfig);
            } else if ("storm".equals(service)) {
                prepareStormService(serviceConfig);
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Unsupported service type: %s.", service));
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_UNSUPORTED);
            }
        }
        switch (type) {
            case SYSTEMCTL:
                String info = systemctl(cmd, service);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Run systemctl success, cmd: %s, service: %s, result: %s.",
                            cmd, service, info));
                }
                return info;
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported service's type: %s.", type.name()));
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_UNSUPPORTED_OPERATE);
        }
    }

    private JSONObject serviceStatusRestJson(String machineIp) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(machineIp)) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        RestClientInvoke invoke = new RestClientInvoke();
        try {
            int restPort = env.getProperty("restful.port", Integer.class, 9999);
            String url = String.format("http://%s:%d/rest/server/status/local", machineIp, restPort);
            JSONObject json = invoke.get(url,
                    JSONObject.class);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("From %s response: %s.", url, json.toJSONString()));
            }
            return json.getJSONObject("data");
        } catch (RestInvokeException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceErrorException(UserInterfaceErrors.SERVICE_STATUS_FAIL);
        } finally {
            invoke.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#serviceStatusRest(String)
     */
    @Override
    public Map<String, ServiceStatus> serviceStatusRest(String machineIp) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(machineIp)) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        JSONObject data = serviceStatusRestJson(machineIp);
        Map<String, ServiceStatus> status = new HashMap<>();
        if (data.containsKey("zookeeper")) {
            JSONObject zookeeper = data.getJSONObject("zookeeper");
            status.put("zookeeper", new ServiceStatus(zookeeper.getBooleanValue("enabled"),
                    zookeeper.getBooleanValue("active")));
        }
        if (data.containsKey("storm")) {
            JSONObject storm = data.getJSONObject("storm");
            status.put("storm", new ServiceStatus(storm.getBooleanValue("enabled"),
                    storm.getBooleanValue("active")));
        }
        return status;
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#serviceStatusLocal(String)
     */
    @Override
    public ServiceStatus serviceStatusLocal(String service) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(service)) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
        if (!service.endsWith(".service")) {
            service = String.format("%s.service", service);
        }
        boolean enabled, active = false;
        String result = serviceLocal(ServiceType.SYSTEMCTL, "is-enabled", service);
        enabled = result.startsWith("enabled");
        if (enabled) {
            result = serviceLocal(ServiceType.SYSTEMCTL, "is-active", service);
            active = result.startsWith("active");
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Service: %s, enabled: %s, active: %s.", service, enabled, active));
        }
        return new ServiceStatus(enabled, active);
    }
}
