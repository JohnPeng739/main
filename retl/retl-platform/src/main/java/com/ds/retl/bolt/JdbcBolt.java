package com.ds.retl.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.jdbc.JdbcManager;
import com.ds.retl.jdbc.JdbcOperate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by john on 2017/9/12.
 */
public class JdbcBolt extends BasePersistBolt {
    private static final Log logger = LogFactory.getLog(JdbcBolt.class);

    private List<JdbcOperate> dataOperates = null;
    private JdbcOperate errorOperate = null;

    public JdbcBolt() {
        super();
        this.dataOperates = new ArrayList<>();
    }

    public JdbcBolt(JSONObject configuration) {
        super(configuration);
        this.dataOperates = new ArrayList<>();
    }

    @Override
    public void initialize(Map stormConf, JSONObject configuration) {
        // 初始化数据源（如有必要）
        String dataSourcesStr = (String) stormConf.get("dataSources");
        if (!StringUtils.isBlank(dataSourcesStr)) {
            JSONArray dataSourcesJson = JSON.parseArray(dataSourcesStr);
            try {
                JdbcManager.getManager().initManager(dataSourcesJson);
            } catch (SQLException ex) {
                String message = String.format("Initialize JDBC Manager fail, from JdbcBolt, dataSources: %s.", dataSourcesStr);
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new IllegalArgumentException(message);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Initialize JDBC Manager successfully, dataSources: %s.", dataSourcesStr));
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
                String message = String.format("Initialize JDBC Operate fail, dataSource: %s, configuration: %s.",
                        dataSourceName, dataSourcesStr);
                if (logger.isErrorEnabled()) {
                    logger.error(message, ex);
                }
                throw new IllegalArgumentException(message);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Initialize JDBC Operate successfully, dataSource: %s, configuration: %s.",
                    dataSourceName, dataSourcesStr));
        }
    }

    @Override
    public void saveData2Db(JSONObject managedJson, JSONObject data) throws Exception {
        super.saveMangedTimeData(managedJson, data);
        for (JdbcOperate operate : dataOperates) {
            operate.saveData2Db(data);
        }
    }

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
