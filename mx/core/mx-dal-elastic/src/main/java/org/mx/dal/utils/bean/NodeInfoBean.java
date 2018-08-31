package org.mx.dal.utils.bean;

import org.mx.StringUtils;
import org.mx.TypeUtils;

/**
 * 描述： ES集群节点信息类定义
 *
 * @author john peng
 * Date time 2018/8/30 下午9:50
 */
public class NodeInfoBean {
    private String ip, nodeRole, name;
    private boolean isMaster = false;
    private float heapPercent, ramPercent, cpu, load_1m, load_5m, load_15m;

    public NodeInfoBean() {
        super();
    }

    public NodeInfoBean(String line) {
        this();
        if (!StringUtils.isBlank(line)) {
            String[] segs = line.split(" ");
            ip = segs[0];
            heapPercent = TypeUtils.string2Float(segs[1], 0);
            ramPercent = TypeUtils.string2Float(segs[2], 0);
            cpu = TypeUtils.string2Float(segs[3], 0);
            load_1m = TypeUtils.string2Float(segs[4], 0);
            load_5m = TypeUtils.string2Float(segs[5], 0);
            load_15m = TypeUtils.string2Float(segs[6], 0);
            nodeRole = segs[7];
            isMaster = "*".equalsIgnoreCase(segs[8]);
            name = segs[9];
        }
    }

    public String getIp() {
        return ip;
    }

    public String getNodeRole() {
        return nodeRole;
    }

    public String getName() {
        return name;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public float getHeapPercent() {
        return heapPercent;
    }

    public float getRamPercent() {
        return ramPercent;
    }

    public float getCpu() {
        return cpu;
    }

    public float getLoad_1m() {
        return load_1m;
    }

    public float getLoad_5m() {
        return load_5m;
    }

    public float getLoad_15m() {
        return load_15m;
    }
}
