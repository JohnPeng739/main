package org.mx.dal.utils;

import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： MongoDB配置信息对象定义
 *
 * @author john peng
 * Date time 2018/12/28 1:21 PM
 */
public class MongoDbConfigBean {
    @Value("${mongo.application:}")
    private String application;
    @Value("${mongo.description:}")
    private String description;
    @Value("${mongo.credential.user:}")
    private String credentialUser;
    @Value("${mongo.credential.password:}")
    private String credentialPassword;
    @Value("${mongo.credential.mechanism:SCRAM_SHA_1}")
    private String credentialMechanism;
    @Value("${mongo.requiredReplicaSetName:}")
    private String requiredReplicaSetName;
    @Value("${mongo.database:}")
    private String database;
    @Value("${mongo.ssl.keystore:}")
    private String keystore;
    @Value("${mongo.ssl.keystorePassword:}")
    private String keystorePassword;
    @Value("${mongo.ssl.keyManagedPassword:}")
    private String keyManagedPassword;
    @Value("${mongo.ssl.keyAlias:}")
    private String keyAlias;
    @Value("${mongo.alwaysUseMBeans:false}")
    private boolean alwaysUseMBeans;
    @Value("${mongo.ssl::false}")
    private boolean ssl;
    @Value("${mongo.ssl.invalidHostNameAllowed:false}")
    private boolean sslInvalidHostNameAllowed;
    @Value("${mongo.retryWrites:false}")
    private boolean retryWrites;
    @Value("${mongo.cursorFinalizer:true}")
    private boolean cursorFinalizer;
    @Value("${mongo.nodes:0}")
    private int nodeNum;
    @Value("${mongo.localThreshold:15}")
    private int localThreshold;
    @Value("${mongo.connectionsPerHost:100}")
    private int connectionsPerHost;
    @Value("${mongo.minConnectionsPerHost:0}")
    private int minConnectionsPerHost;
    @Value("${mongo.threadsAllowedToBlockForConnectionMultiplier:5}")
    private int threadsAllowedToBlockForConnectionMultiplier;
    @Value("${mongo.connectTimeout:10000}")
    private int connectTimeout;
    @Value("${mongo.maxWaitTime:120000}")
    private int maxWaitTime;
    @Value("${mongo.socketTimeout:0}")
    private int socketTimeout;
    @Value("${mongo.maxConnectionIdleTime:0}")
    private int maxConnectionIdleTime;
    @Value("${mongo.maxConnectionLifeTime:0}")
    private int maxConnectionLifeTime;
    @Value("${mongo.heartbeatSocketTimeout:20000}")
    private int heartbeatSocketTimeout;
    @Value("${mongo.heartbeatConnectTimeout:20000}")
    private int heartbeatConnectTimeout;
    @Value("${mongo.heartbeatFrequency:10000}")
    private int heartbeatFrequency;
    @Value("${mongo.minHeartbeatFrequency:500}")
    private int minHeartbeatFrequency;
    @Value("${mongo.serverSelectionTimeout:30000}")
    private int serverSelectionTimeout;

    private Environment env;

    public MongoDbConfigBean(Environment env) {
        super();
        this.env = env;
    }

    public List<ServerAddress> getNodes() {
        List<ServerAddress> nodes = new ArrayList<>();
        for (int index = 1; index <= nodeNum; index++) {
            String host = env.getProperty(String.format("mongo.nodes.%d.host", index));
            int port = env.getProperty(String.format("mongo.nodes.%d.port", index), Integer.class, 27017);
            nodes.add(new ServerAddress(host, port));
        }
        return nodes;
    }

    public String getApplication() {
        return application;
    }

    public String getDescription() {
        return description;
    }

    public String getCredentialUser() {
        return credentialUser;
    }

    public String getCredentialPassword() {
        return credentialPassword;
    }

    public String getCredentialMechanism() {
        return credentialMechanism;
    }

    public String getRequiredReplicaSetName() {
        return requiredReplicaSetName;
    }

    public String getDatabase() {
        return database;
    }

    public String getKeystore() {
        return keystore;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeyManagedPassword() {
        return keyManagedPassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public boolean isAlwaysUseMBeans() {
        return alwaysUseMBeans;
    }

    public boolean isRetryWrites() {
        return retryWrites;
    }

    public boolean isCursorFinalizer() {
        return cursorFinalizer;
    }

    public boolean isSsl() {
        return ssl;
    }

    public boolean isSslInvalidHostNameAllowed() {
        return sslInvalidHostNameAllowed;
    }

    public int getLocalThreshold() {
        return localThreshold;
    }

    public int getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public int getMinConnectionsPerHost() {
        return minConnectionsPerHost;
    }

    public int getThreadsAllowedToBlockForConnectionMultiplier() {
        return threadsAllowedToBlockForConnectionMultiplier;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getMaxConnectionIdleTime() {
        return maxConnectionIdleTime;
    }

    public int getMaxConnectionLifeTime() {
        return maxConnectionLifeTime;
    }

    public int getHeartbeatSocketTimeout() {
        return heartbeatSocketTimeout;
    }

    public int getHeartbeatConnectTimeout() {
        return heartbeatConnectTimeout;
    }

    public int getHeartbeatFrequency() {
        return heartbeatFrequency;
    }

    public int getMinHeartbeatFrequency() {
        return minHeartbeatFrequency;
    }

    public int getServerSelectionTimeout() {
        return serverSelectionTimeout;
    }
}
