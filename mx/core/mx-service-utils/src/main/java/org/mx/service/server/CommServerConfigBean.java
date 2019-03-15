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

    public TcpServerConfig tcpServerConfig(Environment env, String prefix) {
        TcpServerConfig config = new TcpServerConfig(
                env.getProperty(String.format("%s.port", prefix), Integer.class, 9996),
                env.getProperty(String.format("%s.maxLength", prefix), Integer.class, 1024),
                env.getProperty(String.format("%s.packet.wrapper", prefix)),
                env.getProperty(String.format("%s.receiver", prefix)));
        config.setSoTimeout(env.getProperty(String.format("%s.soTimeout", prefix), Integer.class, 0));
        config.setSendBufferSize(env.getProperty(String.format("%s.sendBufferSize", prefix), Integer.class, 146988));
        config.setReceiveBufferSize(env.getProperty(String.format("%s.recvBufferSize", prefix), Integer.class, 408300));
        config.setReuseAddress(env.getProperty(String.format("%s.reuseAddress", prefix), Boolean.class, true));
        config.setTrafficClass(env.getProperty(String.format("%s.trafficClass", prefix), Integer.class, 0));
        config.setKeepAlive(env.getProperty(String.format("%s.keepAlive", prefix), Boolean.class, false));
        config.setNoDelay(env.getProperty(String.format("%s.noDelay", prefix), Boolean.class, false));
        config.setOobInline(env.getProperty(String.format("%s.oobInline", prefix), Boolean.class, false));
        config.setSoLinger(env.getProperty(String.format("%s.soLinger", prefix), Integer.class, -1));
        return config;
    }

    public List<ServerConfig> getTcpServers() {
        List<ServerConfig> serverConfigs = new ArrayList<>();
        for (int index = 1; index <= tcpServerNum; index++) {
            TcpServerConfig config = tcpServerConfig(env, String.format("tcp.servers.%d", index));
            serverConfigs.add(config);
        }
        return serverConfigs;
    }

    public UdpServerConfig udpServerConfig(Environment env, String prefix) {
        UdpServerConfig config = new UdpServerConfig(
                env.getProperty(String.format("%s.port", prefix), Integer.class, 9996),
                env.getProperty(String.format("%s.maxLength", prefix), Integer.class, 1024),
                env.getProperty(String.format("%s.packet.wrapper", prefix)),
                env.getProperty(String.format("%s.receiver", prefix)));
        config.setSoTimeout(env.getProperty(String.format("%s.soTimeout", prefix), Integer.class, 0));
        config.setSendBufferSize(env.getProperty(String.format("%s.sendBufferSize", prefix), Integer.class, 65507));
        config.setReceiveBufferSize(env.getProperty(String.format("%s.recvBufferSize", prefix), Integer.class, 65507));
        config.setReuseAddress(env.getProperty(String.format("%s.reuseAddress", prefix), Boolean.class, false));
        config.setTrafficClass(env.getProperty(String.format("%s.trafficClass", prefix), Integer.class, 0));
        config.setBroadcast(env.getProperty(String.format("%s.broadcast", prefix), Boolean.class, true));
        return config;
    }

    public List<ServerConfig> getUdpServers() {
        List<ServerConfig> serverConfigs = new ArrayList<>();
        for (int index = 1; index <= udpServerNum; index++) {
            UdpServerConfig config = udpServerConfig(env, String.format("udp.servers.%d", index));
            serverConfigs.add(config);
        }
        return serverConfigs;
    }

    public static class ServerConfig {
        private int port, soTimeout, sendBufferSize, receiveBufferSize, trafficClass, maxLength;
        private boolean reuseAddress;
        private String packetWrapper, receiver;

        public ServerConfig() {
            super();
        }

        public ServerConfig(int port, int maxLength, String packetWrapper, String receiver) {
            super();
            this.port = port;
            this.maxLength = maxLength;
            this.packetWrapper = packetWrapper;
            this.receiver = receiver;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public int getPort() {
            return port;
        }

        public String getPacketWrapper() {
            return packetWrapper;
        }

        public String getReceiver() {
            return receiver;
        }

        public int getSoTimeout() {
            return soTimeout;
        }

        public void setSoTimeout(int soTimeout) {
            this.soTimeout = soTimeout;
        }

        public int getSendBufferSize() {
            return sendBufferSize;
        }

        public void setSendBufferSize(int sendBufferSize) {
            this.sendBufferSize = sendBufferSize;
        }

        public int getReceiveBufferSize() {
            return receiveBufferSize;
        }

        public void setReceiveBufferSize(int receiveBufferSize) {
            this.receiveBufferSize = receiveBufferSize;
        }

        public int getTrafficClass() {
            return trafficClass;
        }

        public void setTrafficClass(int trafficClass) {
            this.trafficClass = trafficClass;
        }

        public boolean isReuseAddress() {
            return reuseAddress;
        }

        public void setReuseAddress(boolean reuseAddress) {
            this.reuseAddress = reuseAddress;
        }
    }

    public static class TcpServerConfig extends ServerConfig {
        private boolean keepAlive = false, oobInline = false, noDelay = false;
        private int soLinger = -1;

        public TcpServerConfig() {
            this(9996, 1024, null, null);
        }

        public TcpServerConfig(int port, int maxLength, String packetWrapper, String receiver) {
            super(port, maxLength, packetWrapper, receiver);
            super.setReceiveBufferSize(408300);
            super.setSendBufferSize(146988);
            super.setReuseAddress(true);
            super.setTrafficClass(0);
            super.setSoTimeout(0);
        }

        public boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
        }

        public boolean isOobInline() {
            return oobInline;
        }

        public void setOobInline(boolean oobInline) {
            this.oobInline = oobInline;
        }

        public boolean isNoDelay() {
            return noDelay;
        }

        public void setNoDelay(boolean noDelay) {
            this.noDelay = noDelay;
        }

        public int getSoLinger() {
            return soLinger;
        }

        public void setSoLinger(int soLinger) {
            this.soLinger = soLinger;
        }
    }

    public static class UdpServerConfig extends ServerConfig {
        private boolean broadcast = true;

        public UdpServerConfig() {
            this(9996, 1024, null, null);
        }

        public UdpServerConfig(int port, int maxLength, String packetWrapper, String receiver) {
            super(port, maxLength, packetWrapper, receiver);
            super.setReceiveBufferSize(65507);
            super.setSendBufferSize(65507);
            super.setReuseAddress(false);
            super.setTrafficClass(0);
            super.setSoTimeout(0);
        }

        public boolean isBroadcast() {
            return broadcast;
        }

        public void setBroadcast(boolean broadcast) {
            this.broadcast = broadcast;
        }
    }
}
