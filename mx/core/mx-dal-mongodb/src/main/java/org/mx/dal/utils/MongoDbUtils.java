package org.mx.dal.utils;

import com.mongodb.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;

import java.util.Collections;
import java.util.List;

/**
 * 描述： MongoDB相关工具定义
 *
 * @author john peng
 * Date time 2018/12/28 1:24 PM
 */
public class MongoDbUtils {
    private static final Log logger = LogFactory.getLog(MongoDbUtils.class);

    public static MongoClient createMongoClient(MongoDbConfigBean mongoDbConfigBean) {
        List<ServerAddress> serverAddresses = mongoDbConfigBean.getNodes();
        MongoCredential credential = null;
        String username = mongoDbConfigBean.getCredentialUser(),
                database = mongoDbConfigBean.getDatabase();
        char[] password = mongoDbConfigBean.getCredentialPassword().toCharArray();
        if (!StringUtils.isBlank(mongoDbConfigBean.getCredentialUser())) {
            switch (AuthenticationMechanism.valueOf(mongoDbConfigBean.getCredentialMechanism())) {
                case GSSAPI:
                    credential = MongoCredential.createGSSAPICredential(username);
                    break;
                case MONGODB_CR:
                    credential = MongoCredential.createMongoCRCredential(username, database, password);
                    break;
                case SCRAM_SHA_1:
                    credential = MongoCredential.createScramSha1Credential(username, database, password);
                    break;
                case MONGODB_X509:
                    credential = MongoCredential.createMongoX509Credential(username);
                    break;
                case PLAIN:
                default:
                    credential = MongoCredential.createCredential(username, database, password);
                    break;
            }
        }
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if (!StringUtils.isBlank(mongoDbConfigBean.getApplication())) {
            builder.applicationName(mongoDbConfigBean.getApplication());
        }
        if (!StringUtils.isBlank(mongoDbConfigBean.getDescription())) {
            builder.description(mongoDbConfigBean.getDescription());
        }
        if (mongoDbConfigBean.getLocalThreshold() >= 0) {
            builder.localThreshold(mongoDbConfigBean.getLocalThreshold());
        }
        if (mongoDbConfigBean.getConnectionsPerHost() > 0) {
            builder.connectionsPerHost(mongoDbConfigBean.getConnectionsPerHost());
        }
        if (mongoDbConfigBean.getMinConnectionsPerHost() >= 0) {
            builder.minConnectionsPerHost(mongoDbConfigBean.getMinConnectionsPerHost());
        }
        if (mongoDbConfigBean.getThreadsAllowedToBlockForConnectionMultiplier() > 0) {
            builder.threadsAllowedToBlockForConnectionMultiplier(mongoDbConfigBean.getThreadsAllowedToBlockForConnectionMultiplier());
        }
        if (mongoDbConfigBean.getConnectTimeout() >= 0) {
            builder.connectTimeout(mongoDbConfigBean.getHeartbeatConnectTimeout());
        }
        if (mongoDbConfigBean.getMaxWaitTime() >= 0) {
            builder.maxWaitTime(mongoDbConfigBean.getMaxWaitTime());
        }
        if (mongoDbConfigBean.getSocketTimeout() >= 0) {
            builder.socketTimeout(mongoDbConfigBean.getSocketTimeout());
        }
        if (mongoDbConfigBean.getMaxConnectionIdleTime() >= 0) {
            builder.maxConnectionIdleTime(mongoDbConfigBean.getMaxConnectionIdleTime());
        }
        if (mongoDbConfigBean.getMaxConnectionLifeTime() >= 0) {
            builder.maxConnectionLifeTime(mongoDbConfigBean.getMaxConnectionLifeTime());
        }
        if (mongoDbConfigBean.getHeartbeatConnectTimeout() >= 0) {
            builder.heartbeatConnectTimeout(mongoDbConfigBean.getHeartbeatConnectTimeout());
        }
        if (mongoDbConfigBean.getHeartbeatFrequency() > 0) {
            builder.heartbeatFrequency(mongoDbConfigBean.getHeartbeatFrequency());
        }
        if (mongoDbConfigBean.getHeartbeatSocketTimeout() > 0) {
            builder.heartbeatSocketTimeout(mongoDbConfigBean.getHeartbeatSocketTimeout());
        }
        if (mongoDbConfigBean.getMinHeartbeatFrequency() > 0) {
            builder.minHeartbeatFrequency(mongoDbConfigBean.getMinHeartbeatFrequency());
        }
        if (!StringUtils.isBlank(mongoDbConfigBean.getRequiredReplicaSetName())) {
            builder.requiredReplicaSetName(mongoDbConfigBean.getRequiredReplicaSetName());
        }
        if (mongoDbConfigBean.getServerSelectionTimeout() > 0) {
            builder.serverSelectionTimeout(mongoDbConfigBean.getServerSelectionTimeout());
        }
        builder.cursorFinalizerEnabled(mongoDbConfigBean.isCursorFinalizer())
                .sslEnabled(mongoDbConfigBean.isSsl())
                .sslInvalidHostNameAllowed(mongoDbConfigBean.isSslInvalidHostNameAllowed())
                .alwaysUseMBeans(mongoDbConfigBean.isAlwaysUseMBeans());
        MongoClientOptions options = builder.build();
        if (serverAddresses.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("There are not any server nodes configured.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        } else if (serverAddresses.size() == 1) {
            ServerAddress addr = serverAddresses.get(0);
            return credential != null ? new MongoClient(addr, Collections.singletonList(credential), options) : new MongoClient(addr, options);
        } else {
            return credential != null ? new MongoClient(serverAddresses, Collections.singletonList(credential), options) : new MongoClient(serverAddresses, options);
        }
    }
}
