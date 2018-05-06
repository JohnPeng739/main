package org.mx.kbm.rest.vo.server;

/**
 * 描述： 服务器统计量值对象定义
 *
 * @author John.Peng
 *         Date time 2018/4/20 上午9:26
 */
public class ServerStatVO {
    private String pid, name, desc, uri;
    private long threads;
    private float cpuRate, memoryRate;

    public String getPid() {
        return pid;
    }

    public long getThreads() {
        return threads;
    }

    public float getCpuRate() {
        return cpuRate;
    }

    public float getMemoryRate() {
        return memoryRate;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setThreads(long threads) {
        this.threads = threads;
    }

    public void setCpuRate(float cpuRate) {
        this.cpuRate = cpuRate;
    }

    public void setMemoryRate(float memoryRate) {
        this.memoryRate = memoryRate;
    }
}
