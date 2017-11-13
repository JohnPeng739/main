package com.ds.retl.rest.vo.server;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 本机服务器配置信息值对象定义
 *
 * @author : john.peng created on date : 2017/11/2
 */
public class ServerInfoVO {
    private String machineName, machineIp;
    private Zookeeper zookeeper;
    private Storm storm;

    public class Zookeeper {
        private boolean cluster = false;
        private int serverNo = 1;
        private String dataDir = "/opt/zookeeper/data";
        private List<String> servers;

        public Zookeeper() {
            super();
            this.servers = new ArrayList<>();
        }

        public Zookeeper(JSONObject json) {
            this();
            if (json != null) {
                this.cluster = json.getBooleanValue("cluster");
                this.serverNo = json.getIntValue("serverNo");
                if (this.serverNo <= 0) {
                    this.serverNo = 1;
                }
                this.dataDir = json.getString("dataDir");
                this.servers = json.getObject("servers", List.class);
            }
        }

        public void setCluster(boolean cluster) {
            this.cluster = cluster;
        }

        public void setServerNo(int serverNo) {
            this.serverNo = serverNo;
        }

        public void setDataDir(String dataDir) {
            this.dataDir = dataDir;
        }

        public void setServers(List<String> servers) {
            this.servers = servers;
        }

        public boolean isCluster() {
            return cluster;
        }

        public int getServerNo() {
            return serverNo;
        }

        public String getDataDir() {
            return dataDir;
        }

        public List<String> getServers() {
            return servers;
        }
    }

    public class Storm {
        private List<String> services, zookeepers, nimbuses;
        private String dataDir = "/opt/storm/data";
        private int slots = 10, startPort = 6700;

        public Storm() {
            super();
            this.services = new ArrayList<>();
            this.zookeepers = new ArrayList<>();
            this.nimbuses = new ArrayList<>();
        }

        public Storm(JSONObject json) {
            this();
            if (json != null) {
                this.services = json.getObject("services", List.class);
                this.zookeepers = json.getObject("zookeepers", List.class);
                this.nimbuses = json.getObject("nimbuses", List.class);
                this.dataDir = json.getString("dataDir");
                this.slots = json.getIntValue("slots");
                if (this.slots <= 0) {
                    this.slots = 10;
                }
                this.startPort = json.getIntValue("startPort");
                if (this.startPort < 1024) {
                    this.startPort = 6700;
                }
            }
        }

        public void setServices(List<String> services) {
            this.services = services;
        }

        public void setZookeepers(List<String> zookeepers) {
            this.zookeepers = zookeepers;
        }

        public void setNimbuses(List<String> nimbuses) {
            this.nimbuses = nimbuses;
        }

        public void setDataDir(String dataDir) {
            this.dataDir = dataDir;
        }

        public void setSlots(int slots) {
            this.slots = slots;
        }

        public void setStartPort(int startPort) {
            this.startPort = startPort;
        }

        public List<String> getServices() {
            return services;
        }

        public List<String> getZookeepers() {
            return zookeepers;
        }

        public List<String> getNimbuses() {
            return nimbuses;
        }

        public String getDataDir() {
            return dataDir;
        }

        public int getSlots() {
            return slots;
        }

        public int getStartPort() {
            return startPort;
        }
    }

    public ServerInfoVO() {
        super();
    }

    public ServerInfoVO(JSONObject json) {
        this();
        if (json != null) {
            this.machineName = json.getString("machineName");
            this.machineIp = json.getString("machineIp");
            JSONObject zookeeperJson = json.getJSONObject("zookeeper");
            this.zookeeper = new Zookeeper(zookeeperJson);
            JSONObject stormJson = json.getJSONObject("storm");
            this.storm = new Storm(stormJson);
        }
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public void setZookeeper(Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    public void setStorm(Storm storm) {
        this.storm = storm;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public Storm getStorm() {
        return storm;
    }
}
