package org.mx.dal.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： Elastic配置对象
 *
 * @author john peng
 * Date time 2018/7/20 下午4:34
 */
public class ElasticConfigBean {
    private static final Log logger = LogFactory.getLog(ElasticConfigBean.class);

    @Value("${elastic.index.shards:3}")
    private int shards;
    @Value("${elastic.index.replicas:2}")
    private int replicas;
    @Value("${elastic.entity.base:}")
    private String entityBasePackage;
    @Value("${elastic.servers:0}")
    private int serverNum;

    private Environment env;

    public ElasticConfigBean(Environment env) {
        super();
        this.env = env;
    }

    public int getShards() {
        return shards;
    }

    public int getReplicas() {
        return replicas;
    }

    public String[] getEntityBasePackages() {
        return StringUtils.split(entityBasePackage);
    }

    public int getServerNum() {
        return serverNum;
    }

    public List<ElasticServerConfig> getServerConfigs() {
        List<ElasticServerConfig> serverConfigs = new ArrayList<>();
        for (int index = 1; index <= serverNum; index++) {
            String protocol = env.getProperty(String.format("elastic.servers.%d.protocol", index));
            String server = env.getProperty(String.format("elastic.servers.%d.server", index));
            int port = env.getProperty(String.format("elastic.servers.%d.port", index), Integer.class, 9200);
            if (StringUtils.isBlank(protocol) || StringUtils.isBlank(server)) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Elastic server config invalid: %s://%s:%d", protocol, server, port));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            serverConfigs.add(new ElasticServerConfig(protocol, server, port));
        }
        return serverConfigs;
    }

    public class ElasticServerConfig {
        private String protocol, server;
        private int port;

        public ElasticServerConfig(String protocol, String server, int port) {
            super();
            this.protocol = protocol;
            this.server = server;
            this.port = port;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getServer() {
            return server;
        }

        public int getPort() {
            return port;
        }
    }
}
