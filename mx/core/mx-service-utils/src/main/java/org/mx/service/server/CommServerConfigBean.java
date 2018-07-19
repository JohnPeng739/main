package org.mx.service.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：TCP/UDP通信部分配置对象
 *
 * @author john peng
 * Date time 2018/7/19 下午4:29
 */
public class CommServerConfigBean {
    @Value("${tcp.enabled:false}")
    private boolean tcpEnabled;
    @Value("${tcp.servers.num:0}")
    private int tcpServerNum;
    @Value("${udp.enabled:false}")
    private boolean udpEnabled;
    @Value("${udp.servers.num:0}")
    private int udpServerNum;

    private Environment env;

    public CommServerConfigBean(Environment env) {
        super();
        this.env = env;
    }

    public boolean isTcpEnabled() {
        return tcpEnabled;
    }

    public int getTcpServerNum() {
        return tcpServerNum;
    }

    public boolean isUdpEnabled() {
        return udpEnabled;
    }

    public int getUdpServerNum() {
        return udpServerNum;
    }

    public List<ServerConfig> getTcpServers() {
        List<ServerConfig> serverConfigs = new ArrayList<>();
        for (int index = 1; index <= tcpServerNum; index ++) {
            serverConfigs.add(new ServerConfig(
                    env.getProperty(String.format("tcp.servers.%d.port", index), Integer.class, 9996),
                    env.getProperty(String.format("tcp.servers.%d.maxLength", index), Integer.class, 8 * 1024),
                    env.getProperty(String.format("tcp.servers.%d.maxTimeout", index), Integer.class, -1),
                    env.getProperty(String.format("tcp.servers.%d.packet.wrapper", index)),
                    env.getProperty(String.format("tcp.servers.%d.receiver", index))));
        }
        return serverConfigs;
    }

    public List<ServerConfig> getUdpServers() {
        List<ServerConfig> serverConfigs = new ArrayList<>();
        for (int index = 1; index <= udpServerNum; index ++) {
            serverConfigs.add(new ServerConfig(
                    env.getProperty(String.format("udp.servers.%d.port", index), Integer.class, 9996),
                    env.getProperty(String.format("udp.servers.%d.maxLength", index), Integer.class, 8 * 1024),
                    env.getProperty(String.format("udp.servers.%d.maxTimeout", index), Integer.class, -1),
                    env.getProperty(String.format("udp.servers.%d.packet.wrapper", index)),
                    env.getProperty(String.format("udp.servers.%d.receiver", index))));
        }
        return serverConfigs;
    }

    public class ServerConfig {
        private int port, maxLength, maxTimeout;
        private String packetWrapper, receiver;

        public ServerConfig(int port, int maxLength, int maxTimeout, String packetWrapper, String receiver) {
            super();
            this.port =port;
            this.maxLength = maxLength;
            this.maxTimeout = maxTimeout;
            this.packetWrapper = packetWrapper;
            this.receiver = receiver;
        }

        public int getPort() {
            return port;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public int getMaxTimeout() {
            return maxTimeout;
        }

        public String getPacketWrapper() {
            return packetWrapper;
        }

        public String getReceiver() {
            return receiver;
        }
    }
}
