/**
 *
 */
package org.mx.storm.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.*;

import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author john
 */
public class CommonTopology {
    private StormConfigure stormConfig = null;

    /**
     * 默认的构造函数
     */
    public CommonTopology() {
        super();
    }

    private void initialize(String configFile) throws Exception {
        FileInputStream fis = new FileInputStream(configFile);
        InputStreamReader isr = new InputStreamReader(fis);
        JSONReader reader = new JSONReader(isr);
        String json = reader.readString();
        reader.close();
        stormConfig = JSON.parseObject(json, StormConfigure.class);
    }

    private void putProperties(Config config) {
        if (stormConfig == null || stormConfig.getProperties().size() <= 0) {
            return;
        }
        for (Object key : stormConfig.getProperties().keySet().toArray()) {
            Object value = stormConfig.getProperties().get(key);
            config.put(key.toString(), value);
        }
    }

    private void createSpouts(TopologyBuilder builder) throws Exception {
        if (stormConfig == null || stormConfig.getSpouts().size() <= 0) {
            return;
        }
        for (SpoutConfigure sc : stormConfig.getSpouts()) {
            builder.setSpout(sc.getName(), (IRichSpout) sc.getSpoutClass()
                    .newInstance(), sc.getParallelNum());
        }
    }

    private void createBolts(TopologyBuilder builder) throws Exception {
        if (stormConfig == null || stormConfig.getBolts().size() <= 0) {
            return;
        }
        for (BoltConfigure bc : stormConfig.getBolts()) {
            Object bolt = bc.getBoltClass().newInstance();
            BoltDeclarer bd = null;
            if (bolt instanceof IBasicBolt) {
                bd = builder.setBolt(bc.getName(), (IBasicBolt) bolt,
                        bc.getParallelNum());
            } else {
                bd = builder.setBolt(bc.getName(), (IRichBolt) bolt,
                        bc.getParallelNum());
            }
            if (BoltConfigure.GROUP_TYPE_SHUFFLE.equals(bc.getGroupType())) {
                if (bc.getStreamId() == null || bc.getStreamId().length() <= 0) {
                    bd.shuffleGrouping(bc.getGroupComponentName());
                } else {
                    bd.shuffleGrouping(bc.getGroupComponentName(),
                            bc.getStreamId());
                }
            }
        }
    }

    private void submit() throws Exception {
        Config config = new Config();
        config.setDebug(stormConfig.isDebug());
        config.setMaxSpoutPending(stormConfig.getMaxSpoutPending());
        config.setMaxTaskParallelism(stormConfig.getMaxTaskParallelism());
        config.setMessageTimeoutSecs(stormConfig.getMessageTimeoutSecs());
        config.setNumAckers(stormConfig.getNumAckers());
        config.setNumWorkers(stormConfig.getNumWorkers());

        putProperties(config);

        TopologyBuilder builder = new TopologyBuilder();
        createSpouts(builder);
        createBolts(builder);

        if (stormConfig.isLocalCluster()) {
            // local cluster
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(stormConfig.getTopologyName(), config,
                    builder.createTopology());
            // wait to quit
            System.out.println("Hit enter to shutdown ...");
            System.in.read();
            cluster.shutdown();
        } else {
            // remote cluster
            StormSubmitter.submitTopology(stormConfig.getTopologyName(),
                    config, builder.createTopology());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length <= 0) {
            System.out.print("Usage: me <json config file>");
        } else {
            CommonTopology topology = new CommonTopology();
            topology.initialize(args[0]);
            topology.submit();
        }
    }

}
