/**
 *
 */
package com.dscomm.storm.sds;


import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

/**
 * @author john
 */
public class SpanRegionCommandTopology {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("receiveSpanRegionCommandSpout",
                new ReceiveSpanRegionCommandSpout(), 1);
        builder.setBolt("dispatchSpanRegionCommandBasicBolt",
                new DispatchSpanRegionCommandBasicBolt(), 1).shuffleGrouping(
                "receiveSpanRegionCommandSpout");
        Config config = new Config();
        config.setDebug(true);
        config.put("mq.url", "tcp://10.48.147.188:61616");
        config.put("mq.user", "ds110");
        config.put("mq.password", "edmund");
        config.put("mq.queue.request", "sdst.span-region-commander");
        config.put("db.driver", "oracle.jdbc.driver.OracleDriver");
        config.put("db.url", "jdbc:oracle:thin:@10.48.4.14:1521/DSDB");
        config.put("db.user", "sdhzk");
        config.put("db.password", "sdhzk");
        config.put(
                "db.query.route",
                "SELECT REGION_CODE, JMS_URL, JMS_USER, JMS_PWD, JMS_QUEUE, TYPE FROM T_SPANREGION");
        config.put("db.query.period", 60);
        config.put("span.to.id", "span.to");
        config.put("span.cc.id", "span.cc");
        config.put("span.type.id", "span.type");

        String name = "spanRegionCommandTopology";
        if (args != null && args.length > 0) {
            // remote cluster mode, the args[0] is the topology's name.
            config.setNumWorkers(1);
            StormSubmitter.submitTopology(name, config,
                    builder.createTopology());
        } else {
            // local cluster mode
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(name, config, builder.createTopology());
            // wait to quit
            System.out.println("Hit enter to shutdown ...");
            System.in.read();
            // cluster.killTopology("sampleTopology");
            cluster.shutdown();
        }
    }
}
