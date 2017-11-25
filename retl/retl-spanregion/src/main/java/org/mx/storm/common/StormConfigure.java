/**
 *
 */
package org.mx.storm.common;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @author john
 */
public class StormConfigure implements Serializable {
    private static final long serialVersionUID = 794456607800475444L;

    private boolean localCluster = true;

    private String topologyName = "SimpleTopolog";
    private int maxSpoutPending = 1, maxTaskParallelism = 1,
            messageTimeoutSecs = 30;
    private int numAckers = 1, numWorkers = 1;
    private boolean debug = false;
    private Properties properties = new Properties();
    private List<SpoutConfigure> spouts = new Vector<SpoutConfigure>();
    private List<BoltConfigure> bolts = new Vector<BoltConfigure>();

    /**
     * 默认的构造函数
     */
    public StormConfigure() {
        super();
    }

    /**
     * @return the localCluster
     */
    public boolean isLocalCluster() {
        return localCluster;
    }

    /**
     * @param localCluster the localCluster to set
     */
    public void setLocalCluster(boolean localCluster) {
        this.localCluster = localCluster;
    }

    /**
     * @return the topologyName
     */
    public String getTopologyName() {
        return topologyName;
    }

    /**
     * @param topologyName the topologyName to set
     */
    public void setTopologyName(String topologyName) {
        this.topologyName = topologyName;
    }

    /**
     * @return the maxSpoutPending
     */
    public int getMaxSpoutPending() {
        return maxSpoutPending;
    }

    /**
     * @param maxSpoutPending the maxSpoutPending to set
     */
    public void setMaxSpoutPending(int maxSpoutPending) {
        this.maxSpoutPending = maxSpoutPending;
    }

    /**
     * @return the maxTaskParallelism
     */
    public int getMaxTaskParallelism() {
        return maxTaskParallelism;
    }

    /**
     * @param maxTaskParallelism the maxTaskParallelism to set
     */
    public void setMaxTaskParallelism(int maxTaskParallelism) {
        this.maxTaskParallelism = maxTaskParallelism;
    }

    /**
     * @return the messageTimeoutSecs
     */
    public int getMessageTimeoutSecs() {
        return messageTimeoutSecs;
    }

    /**
     * @param messageTimeoutSecs the messageTimeoutSecs to set
     */
    public void setMessageTimeoutSecs(int messageTimeoutSecs) {
        this.messageTimeoutSecs = messageTimeoutSecs;
    }

    /**
     * @return the numAckers
     */
    public int getNumAckers() {
        return numAckers;
    }

    /**
     * @param numAckers the numAckers to set
     */
    public void setNumAckers(int numAckers) {
        this.numAckers = numAckers;
    }

    /**
     * @return the numWorkers
     */
    public int getNumWorkers() {
        return numWorkers;
    }

    /**
     * @param numWorkers the numWorkers to set
     */
    public void setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * @return the spouts
     */
    public List<SpoutConfigure> getSpouts() {
        return spouts;
    }

    /**
     * @param spouts the spouts to set
     */
    public void setSpouts(List<SpoutConfigure> spouts) {
        this.spouts = spouts;
    }

    /**
     * @return the bolts
     */
    public List<BoltConfigure> getBolts() {
        return bolts;
    }

    /**
     * @param bolts the bolts to set
     */
    public void setBolts(List<BoltConfigure> bolts) {
        this.bolts = bolts;
    }

}
