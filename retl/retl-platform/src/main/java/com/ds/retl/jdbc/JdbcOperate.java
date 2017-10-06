package com.ds.retl.jdbc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by john on 2017/9/12.
 */
public class JdbcOperate {
    private static final Log logger = LogFactory.getLog(JdbcOperate.class);

    private Connection connection = null;
    private String table, primaryField, primaryKey;
    private Map<String, String> fieldTypes = null;
    private Map<String, String> mapping;

    private String sqlSelect, sqlInsert, sqlUpdate;
    private List<String> insertFields, updateFields;

    private List<JSONObject> insertStats, updateStats;
    private long lastSavedTime = new Date().getTime();
    private int maxCachedSecs = 1, maxCachedSize = 1000;

    public JdbcOperate() {
        super();
        this.insertFields = new ArrayList<>();
        this.updateFields = new ArrayList<>();
        this.fieldTypes = new HashMap<>();
        this.insertStats = new ArrayList<>();
        this.updateStats = new ArrayList<>();
    }

    public JdbcOperate(Connection conn, JSONObject tableJson) throws SQLException {
        this();
        this.connection = conn;

        String table = tableJson.getString("table"),
                key = tableJson.getString("key");
        if (StringUtils.isBlank(table)) {
            throw new SQLException(String.format("Not define table, configure: %s.",
                    tableJson.toJSONString()));
        }
        if (StringUtils.isBlank(key)) {
            throw new SQLException(String.format("Not define primary key, configure: %s.",
                    tableJson.toJSONString()));
        }
        this.table = table;
        this.primaryField = key;
        this.mapping = JSON.parseObject(tableJson.getJSONObject("mapping").toJSONString(), Map.class);
        JSONObject specialTypes = tableJson.getJSONObject("specialTypes");
        this.maxCachedSize = tableJson.getIntValue("maxCachedSize");
        this.maxCachedSecs = tableJson.getIntValue("maxCachedSecs");

        if (specialTypes != null) {
            specialTypes.keySet().forEach(fieldType -> {
                String keyFields = specialTypes.getString(fieldType);
                if (!StringUtils.isBlank(keyFields)) {
                    String[] fields = keyFields.split(",");
                    if (fields != null && fields.length > 0) {
                        for (String field : fields) {
                            fieldTypes.put(field, fieldType);
                        }
                    }
                }
            });
        }
        checkSqlClause();
    }

    public String getTable() {
        return table;
    }

    public String getPrimaryField() {
        return primaryField;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    private void checkSqlClause() throws SQLException {
        if (StringUtils.isBlank(table)) {
            throw new SQLException("The table name is blank.");
        }
        if (StringUtils.isBlank(primaryField)) {
            throw new SQLException("Not define the primary primaryField field.");
        }
        if (mapping == null || mapping.isEmpty()) {
            throw new SQLException("Not define the field's list.");
        }
        if (sqlInsert == null) {
            List<String> fields = new ArrayList<>();
            mapping.keySet().forEach(key -> {
                fields.add(mapping.get(key));
                insertFields.add(key);
            });
            sqlInsert = String.format("insert into %s(%s) values(%s)", table,
                    StringUtils.merge(fields, StringUtils.DEFAULT_SEPARATOR),
                    StringUtils.merge("?", fields.size(), StringUtils.DEFAULT_SEPARATOR));
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Initialize insert SQL: %s.", sqlInsert));
            }
        }
        if (sqlUpdate == null) {
            List<String> fields = new ArrayList<>();
            mapping.keySet().forEach(key -> {
                String field = mapping.get(key);
                if (primaryField.equals(field)) {
                    // primary primaryField, ignored
                    primaryKey = key;
                    return;
                }
                fields.add(field);
                updateFields.add(key);
            });
            sqlUpdate = String.format("update %s set %s %s where %s = ?", table,
                    StringUtils.merge(fields, " = ?, "), "= ?", primaryField);
            updateFields.add(primaryKey);
        }
        if (sqlSelect == null) {
            sqlSelect = String.format("select %s from %s where %s = ?", primaryField, table, primaryField);
        }
    }

    private boolean exist(JSONObject data) throws SQLException {
        boolean found = false;
        String id = data.getString(primaryField);
        if (!StringUtils.isBlank(id)) {
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect);
            psSelect.setString(1, data.getString(primaryField));
            ResultSet rs = psSelect.executeQuery();
            found = rs.next();
            psSelect.close();
        }
        return found;
    }

    private void flushData(String sql, List<JSONObject> dataList, List<String> fields) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (JSONObject data : dataList) {
            for (int index = 0; index < fields.size(); index++) {
                String field = fields.get(index);
                ps.setObject(index + 1, prepareValue(field, data));
            }
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
        dataList.clear();
    }

    private void flushData() throws SQLException {
        if (insertStats.size() + updateStats.size() < maxCachedSize &&
                new Date().getTime() - lastSavedTime < maxCachedSecs * 1000) {
            return;
        }
        connection.setAutoCommit(false);
        try {
            // 先保存update
            flushData(sqlUpdate, updateStats, updateFields);
            //再保存insert
            flushData(sqlInsert, insertStats, insertFields);
            connection.commit();
            lastSavedTime = new Date().getTime();
        } catch (SQLException ex) {
            connection.rollback();
        }
    }

    public synchronized void saveData2Db(JSONObject data) throws SQLException {
        if (data == null) {
            return;
        }
        // 先检测该记录是否存在
        if (exist(data)) {
            // 数据已经存在，进行更新操作
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate);
            for (int index = 0; index < updateFields.size(); index++) {
                String field = updateFields.get(index);
                psUpdate.setObject(index + 1, prepareValue(field, data));
            }
            psUpdate.executeUpdate();
            psUpdate.close();
        } else {
            // 数据不存在，进行插入操作
            PreparedStatement psInsert = connection.prepareStatement(sqlInsert);
            for (int index = 0; index < insertFields.size(); index++) {
                String field = insertFields.get(index);
                psInsert.setObject(index + 1, prepareValue(field, data));
            }
            psInsert.executeUpdate();
            psInsert.close();
        }
    }

    public synchronized void saveData2Db2(JSONObject data) throws SQLException {
        if (data == null) {
            return;
        }
        // 先检测该记录是否存在
        if (exist(data)) {
            // 数据已经存在，进行更新操作
            updateStats.add(data);
        } else {
            // 数据不存在，进行插入操作
            insertStats.add(data);
        }
        flushData();
    }

    private Object prepareValue(String field, JSONObject data) {
        Object value = data.get(field);
        if (value == null) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The data is nll. field: %s, data: %s.", field, data.toJSONString()));
            }
        }
        String type = fieldTypes.get(field);
        if (type == null) {
            type = "";
        }
        switch (type) {
            case "DATE":
                value = new java.sql.Date((long) value);
                break;
            case "TIME":
                value = new java.sql.Time((long) value);
                break;
            case "TIMESTAMP":
                value = new java.sql.Timestamp((long) value);
                break;
            default:
                break;
        }
        return value;
    }
}
