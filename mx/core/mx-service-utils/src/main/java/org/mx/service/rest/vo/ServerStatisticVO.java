package org.mx.service.rest.vo;

/**
 * 描述： 服务器统计量值对象定义
 *
 * @author John.Peng
 * Date time 2018/4/20 上午9:26
 */
public class ServerStatisticVO {
    private String pid, name, desc, uri;
    private long threads;
    private float cpuRate, memoryRate;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getThreads() {
        return threads;
    }

    public void setThreads(long threads) {
        this.threads = threads;
    }

    public float getCpuRate() {
        return cpuRate;
    }

    public void setCpuRate(float cpuRate) {
        this.cpuRate = cpuRate;
    }

    public float getMemoryRate() {
        return memoryRate;
    }

    public void setMemoryRate(float memoryRate) {
        this.memoryRate = memoryRate;
    }
}
