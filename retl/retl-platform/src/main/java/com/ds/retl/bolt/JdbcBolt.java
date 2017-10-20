package com.ds.retl.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.jdbc.JdbcManager;
import com.ds.retl.jdbc.JdbcOperate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 基于JDBC关系型数据库存储数据的Bolt类，能够将处理后的数据或者错误信息持久化存储到对应的数据库表中。
 *
 * @author : john.peng created on date : 2017/9/12
 */
public class JdbcBolt extends BasePersistBolt {
    private static final Log logger = LogFactory.getLog(JdbcBolt.class);

    private List<JdbcOperate> dataOperates = null;
    private JdbcOperate errorOperate = null;

    /**
     * 默认的构造函数
     */
    public JdbcBolt() {
        super();
        this.dataOperates = new ArrayList<>();
    }

    /**
     * 默认的构造函数
     *
     * @param configuration 初始化配置信息
     */
    public JdbcBolt(JSONObject configuration) {
        super(configuration);
        this.dataOperates = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see BasePersistBolt#initialize(Map, JSONObject)
     */
    @Override
    public void initialize(Map conf, JSONObject configuration) {
        try {
            JdbcManager.getManager().initManager(conf);
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Initialize JDBC Manager fail.");
        }

        String dataSourceName = configuration.getString("dataSource");
        JSONArray tablesJson = configuration.getJSONArray("tables");
        if (tablesJson != null && !tablesJson.isEmpty()) {
            try {
                Connection connection = JdbcManager.getManager().getConnection(dataSourceName);
                for (int index = 0; index < tablesJson.size(); index++) {
                    JSONObject tableJson = tablesJson.getJSONObject(index);
                    JdbcOperate operate = new JdbcOperate(connection, tableJson);
                    this.dataOperates.add(operate);
                }
                this.errorOperate = JdbcManager.getManager().getErrorOperate(dataSourceName);
            } catch (SQLException ex) {
                String message = String.format("Initialize JDBC Operate fail, dataSource: %s.",
                        dataSourceName);
                if (logger.isErrorEnabled()) {
                    logger.error(message, ex);
                }
                throw new IllegalArgumentException(message);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Initialize JDBC Operate successfully, dataSource: %s.",
                    dataSourceName));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BasePersistBolt#saveData2Db(JSONObject, JSONObject)
     */
    @Override
    public void saveData2Db(JSONObject managedJson, JSONObject data) throws Exception {
        super.saveMangedTimeData(managedJson, data);
        for (JdbcOperate operate : dataOperates) {
            operate.saveData2Db(data);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BasePersistBolt#saveError2Db(JSONObject, JSONObject, JSONArray)
     */
    @Override
    public void saveError2Db(JSONObject managedJson, JSONObject data, JSONArray errors) throws Exception {
        super.saveMangedTimeData(managedJson, data);
        for (int index = 0; index < errors.size(); index++) {
            JSONObject error = errors.getJSONObject(index);
            error.put("id", UUID.randomUUID().toString());
            error.put("_structuredTime", data.getLongValue("_structuredTime"));
            error.put("_validatedTime", data.getLongValue("_validatedTime"));
            error.put("_transformedTime", data.getLongValue("_transformedTime"));
            error.put("_errorTime", data.getLongValue("_errorTime"));
            error.put("_persistedTime", new Date().getTime());
            errorOperate.saveData2Db(error);
        }
    }
}
