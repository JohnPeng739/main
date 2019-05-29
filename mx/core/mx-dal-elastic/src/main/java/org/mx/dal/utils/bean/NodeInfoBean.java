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
    private String ip, nodeRole, name; // IP、节点角色、名称
    private boolean isMaster = false; // 是否主用节点
    private float heapPercent, ramPercent, cpu, load_1m, load_5m, load_15m; // 堆内存%、内存%、CPU负载、1分钟负载、5分钟负载、15分钟负载

    /**
     * 默认的构造函数
     */
    public NodeInfoBean() {
        super();
    }

    /**
     * 默认的构造函数
     * @param line 文本数据
     */
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

    /**
     * 获取节点IP地址
     * @return IP地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 获取节点角色
     * @return 角色
     */
    public String getNodeRole() {
        return nodeRole;
    }

    /**
     * 获取节点名称
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取是否主用状态
     * @return 主用返回true，否则返回false
     */
    public boolean isMaster() {
        return isMaster;
    }

    /**
     * 获取堆内存占用百分比
     * @return 堆内存百分比
     */
    public float getHeapPercent() {
        return heapPercent;
    }

    /**
     * 获取内存占用百分比
     * @return 内存百分比
     */
    public float getRamPercent() {
        return ramPercent;
    }

    /**
     * 获取CPU负载
     * @return 负载
     */
    public float getCpu() {
        return cpu;
    }

    /**
     * 获取近1分钟负载
     * @return 负载
     */
    public float getLoad_1m() {
        return load_1m;
    }

    /**
     * 获取近5分钟负载
     * @return 负载
     */
    public float getLoad_5m() {
        return load_5m;
    }

    /**
     * 获取近15分钟负载
     * @return 负载
     */
    public float getLoad_15m() {
        return load_15m;
    }
}
