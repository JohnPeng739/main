package com.ds.retl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.cli.SystemCtlCli;
import com.ds.retl.dal.entity.ConfigJson;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.OperateLogService;
import com.ds.retl.service.ServerManageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

    private String createMachineConfigField(String machineName) {
        String field = String.format("%s.%s", RETL_SERVERS_PREFIX, machineName);
        if (field.length() >= 30) {
            field = field.substring(0, 30);
        }
        return field;
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#getLocalServerConfigInfo()
     */
    @Override
    public JSONObject getLocalServerConfigInfo() throws UserInterfaceErrorException {
        try {
            // 获取本机机器名
            InetAddress addr = InetAddress.getLocalHost();
            String machineName = addr.getHostName(), machineIp = addr.getHostAddress();
            try {
                ConfigJson config = accessor.getByCode(createMachineConfigField(machineName), ConfigJson.class);
                JSONObject json;
                if (config == null || StringUtils.isBlank(config.getConfigContent())) {
                    json = new JSONObject();
                    json.put("machineName", machineName);
                    json.put("machineIp", machineIp);
                } else {
                    try {
                        json = JSON.parseObject(config.getConfigContent());
                    } catch (Exception ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Parse json fail.", config.getConfigContent()), ex);
                        }
                        throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
                    }
                }
                return json;
            } catch (EntityAccessException ex) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
            }
        } catch (UnknownHostException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_HOST_EXCEPTION);
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
        operateLogService.writeLog("写入ZOOKEEPER服务配置信息成功。");
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
            for (int index = 0; index < nimbuses.size(); index ++) {
                nimbuses.set(index, String.format("\"%s\"", nimbuses.get(index)));
            }
            ps.println(String.format("nimbus.seeds: [%s]", StringUtils.merge(nimbuses, ", ")));
            ps.println();
            ps.println(String.format("storm.local.dir: \"%s\"", dataDir));
            ps.println();
            ps.println("supervisor.slots.ports:");
            for (int index = 0; index < slots; index ++) {
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
        operateLogService.writeLog("写入STORM服务配置信息成功。");
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#saveLocalServerConfigInfo(String)
     */
    @Override
    public JSONObject saveLocalServerConfigInfo(String info) throws UserInterfaceErrorException {
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
        prepareZookeeperService(json.getJSONObject("zookeeper"));
        prepareStormService(json.getJSONObject("storm"));
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
            operateLogService.writeLog(String.format("保存本机[name=%s, ip=%s]配置信息成功。", machineName, machineIp));
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
     * @see ServerManageService#getServerConfigInfos()
     */
    @Override
    public Map<String, JSONObject> getServerConfigInfos() throws UserInterfaceErrorException {
        try {
            List<ConfigJson> list = accessor.list(ConfigJson.class);
            Map<String, JSONObject> map = new HashMap<>();
            if (list != null && list.size() > 0) {
                list.forEach(config -> {
                    if (config.getCode().startsWith(RETL_SERVERS_PREFIX)) {
                        String info = config.getConfigContent();
                        try {
                            JSONObject json = JSON.parseObject(info);
                            String machineName = json.getString("machineName");
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
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported systemctl cmd type: %s.", cmd));
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_UNSUPPORTED_OPERATE);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ServerManageService#service(ServiceType, String, String)
     */
    @Override
    public void service(ServiceType type, String cmd, String service) throws UserInterfaceErrorException {
        switch (type) {
            case SYSTEMCTL:
                String info = systemctl(cmd, service);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Run systemctl success, cmd: %s, service: %s, result: %s.",
                            cmd, service, info));
                }
                break;
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported service's type: %s.", type.name()));
                }
                throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_UNSUPPORTED_OPERATE);
        }
    }
}
