package com.ds.retl.spout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.jdbc.JdbcManager;
import com.ds.retl.zookeeper.ZookeeperOperate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.mx.StringUtils;

import java.sql.*;
import java.util.*;

/**
 * Created by john on 2017/9/15.
 */
public class JdbcSpout extends BaseRichSpout {
    private static final Log logger = LogFactory.getLog(JdbcSpout.class);

    private SpoutOutputCollector collector = null;
    private JSONObject configuration = null;
    private Connection connection = null;
    private String table, key, timestamp, timestampNew, ackPath = null;
    private String sqlCount = null, sqlSelectFail = null, sqlSelect = null;
    private Map<String, String> fieldTransform = null;
    private int windowSize = 100, intervalSec = 10;
    private Queue<JSONObject> cache = null;
    private JSONObject managedJson = null;
    private Map<String, JSONObject> pending = null;
    private long lastFetchTime = 0;

    public JdbcSpout() {
        super();
        cache = new LinkedList<>();
        pending = new HashMap<>();
    }

    public JdbcSpout(JSONObject configuration) {
        this();
        this.configuration = configuration;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;

        // 如果可能，初始化Zookeeper
        String zookeeperStr = (String) conf.get("zookeepers");
        if (!StringUtils.isBlank(zookeeperStr)) {
            JSONObject zookeeperConfig = JSON.parseObject(zookeeperStr);
            try {
                ZookeeperOperate.getOperate().initialize(zookeeperConfig);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Initialize Zookeeper fail, configuration: %s", zookeeperStr), ex);
                }
                throw new IllegalArgumentException(ex.getMessage());
            }
        }

        // 如果可能，初始化JDBC连接池
        try {
            JdbcManager.getManager().initManager(conf);
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Initialize JDBC Manager fail.");
        }

        String dataSourceName = configuration.getString("dataSource");
        table = configuration.getString("table");
        key = configuration.getString("key");
        timestamp = configuration.getString("timestamp");
        ackPath = configuration.getString("ackPath");
        windowSize = configuration.getIntValue("windowSize");
        if (windowSize <= 0) {
            windowSize = 100;
        }
        managedJson = new JSONObject();
        managedJson.put("table", table);
        managedJson.put("key", key);
        managedJson.put("timestamp", timestamp);
        managedJson.put("ackPath", ackPath);
        managedJson.put("source", "JDBC");
        intervalSec = configuration.getIntValue("intervalSec");
        String fields = configuration.getString("fields");
        if (StringUtils.isBlank(table) || StringUtils.isBlank(key) || StringUtils.isBlank(timestamp)) {
            throw new IllegalArgumentException(String.format("The JdbcSpout configuration is invalid, configuration: %s.",
                    configuration.toJSONString()));
        }
        if (StringUtils.isBlank(fields)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The JdbcSpout not define the field list, fetch all field.");
            }
            fields = "*";
        }
        try {
            sqlCount = String.format("select count(*),min(%s),max(%s),current_timestamp from %s where %s >= ?",
                    timestamp, timestamp, table, timestamp);
            sqlSelectFail = String.format("select %s from %s where %s = ?",
                    fields, table, key);
            sqlSelect = String.format("select %s from %s where %s >= ? and %s < ?",
                    fields, table, timestamp, timestamp);
            connection = JdbcManager.getManager().getConnection(dataSourceName);
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Get Jdbc connection fail.", ex);
            }
        }
        if (configuration.containsKey("fieldTransform")) {
            fieldTransform = configuration.getObject("fieldTransform", Map.class);
        }
        if (fieldTransform == null) {
            fieldTransform = new HashMap<>();
        }
        if (fieldTransform.containsKey(timestamp)) {
            timestampNew = fieldTransform.get(timestamp);
        } else {
            timestampNew = timestamp;
        }
        // 获取上次成功抽取的时间作为初始开始时间
        List<String> timestamps = ZookeeperOperate.getOperate().getChild(String.format("%s/lastFetchTime", ackPath));
        long lastFetchTime = 0;
        if (timestamps != null) {
            for (String t : timestamps) {
                long ts = Long.valueOf(t);
                lastFetchTime = Math.max(lastFetchTime, ts);
            }
        }
    }

    private int dowithResultset(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        List<String> columns = new ArrayList<>();
        for (int index = 1; index <= metaData.getColumnCount(); index++) {
            columns.add(metaData.getColumnName(index));
        }
        while (rs.next()) {
            JSONObject json = new JSONObject();
            for (String column : columns) {
                if (fieldTransform.containsKey(column)) {
                    // 需要进行字段名转换
                    json.put(fieldTransform.get(column), rs.getObject(column));
                } else {
                    json.put(column, rs.getObject(column));
                }
            }
            cache.add(json);
        }
        return cache.size();
    }

    private void fetchFailRecord() {
        List<String> failKeys = ZookeeperOperate.getOperate().getChild(String.format("%s/fail", ackPath));
        if (failKeys != null && !failKeys.isEmpty()) {
            try {
                PreparedStatement ps = connection.prepareStatement(sqlSelectFail);
                for (String failKey : failKeys) {
                    ps.setString(1, failKey);
                    ResultSet rs = ps.executeQuery();
                    dowithResultset(rs);
                    rs.close();
                }
                ps.close();
            } catch (SQLException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Fetch the fail data with JDBC fail.", ex);
                }
            }
        }
    }

    private void fetchData() {
        try {
            boolean hasData = false;
            long startTime = lastFetchTime, endTime = 0;
            PreparedStatement ps = connection.prepareStatement(sqlCount);
            ps.setTimestamp(1, new Timestamp(startTime));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long count = rs.getLong(1);
                if (count <= 0) {
                } else if (count <= windowSize) {
                    if (rs.getTimestamp(2) != null) {
                        startTime = rs.getTimestamp(2).getTime();
                    }
                    // 留当前时间的前一秒为可能的数据插入冲突作为缓冲空间
                    endTime = rs.getTimestamp(4).getTime() - 1000;
                } else {
                    if (rs.getTimestamp(2) != null) {
                        startTime = rs.getTimestamp(2).getTime();
                    }
                    if (rs.getTimestamp(3) != null) {
                        endTime = rs.getTimestamp(3).getTime();
                        endTime = startTime + (endTime - startTime) * windowSize / count;
                    }
                    if (endTime - startTime < 1000) {
                        endTime += 1000;
                    }
                }
                hasData = count > 0;
            }
            rs.close();
            ps.close();

            if (hasData) {
                ps = connection.prepareStatement(sqlSelect);
                ps.setTimestamp(1, new Timestamp(startTime));
                ps.setTimestamp(2, new Timestamp(endTime));
                lastFetchTime = endTime;
                rs = ps.executeQuery();
                int size = dowithResultset(rs);
                rs.close();
                ps.close();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Fetch JDBC data: %d.", size));
                }
            }

            if (intervalSec <= cache.size() * 50 / 1000) {
                intervalSec = cache.size() * 50 / 1000;
            }
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Fetch data with JDBC fail.", ex);
            }
        }
    }

    @Override
    public void nextTuple() {
        if (cache.isEmpty()) {
            Utils.sleep(2000);
            fetchFailRecord();
            fetchData();
        }
        JSONObject json = cache.poll();
        if (json != null) {
            String messageId = json.getString(key);
            pending.put(messageId, json);
            collector.emit(new Values(managedJson, json.toJSONString()), messageId);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("managedJson", "json"));
    }

    @Override
    public void close() {
        try {
            ZookeeperOperate.getOperate().close();
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Close zookeeper fail.", ex);
            }
        }
        super.close();
    }

    @Override
    public void ack(Object msgId) {
        System.out.println("***************************");
        System.out.println(msgId);
        System.out.println("***************************");
        JSONObject record = pending.remove(msgId);
        if (record == null) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The record[%s] not found, ack exception.", msgId));
            }
            return;
        }
        long ts = record.getTimestamp(timestampNew).getTime();
        // 清除旧的时间戳
        List<String> older = ZookeeperOperate.getOperate().getChild(String.format("%s/lastFetchTime", ackPath));
        if (older != null && !older.isEmpty()) {
            older.forEach(old -> {
                long t = Long.valueOf(old);
                if (t < ts) {
                    ZookeeperOperate.getOperate().delete(String.format("%s/lastFetchTime/%d", ackPath, t));
                }
            });
        }
        ZookeeperOperate.getOperate().createNode(String.format("%s/lastFetchTime/%d", ackPath, ts), "");
        // 判断是否为以前失败的记录，如果是，则清除该记录
        String failIdPath = String.format("%s/fail/%s", ackPath, msgId);
        if (ZookeeperOperate.getOperate().exist(failIdPath)) {
            ZookeeperOperate.getOperate().delete(failIdPath);
        }
        super.ack(msgId);
    }

    @Override
    public void fail(Object msgId) {
        System.out.println("----------------------------");
        System.out.println(msgId);
        System.out.println("----------------------------");
        JSONObject record = pending.remove(msgId);
        if (record == null) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The record[%s] not found, fail exception.", msgId));
            }
            return;
        }
        ZookeeperOperate.getOperate().createNode(String.format("%s/fail/%s", ackPath, String.valueOf(msgId)), "");
        super.fail(msgId);
    }
}
