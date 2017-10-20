package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;
import com.ds.retl.jdbc.JdbcManager;

/**
 * 被包含（存在性）校验规则，支持静态枚举字典和数据库字典表的存在性校验（缓存校验和实时校验）。
 *
 * 缓存校验：
 * {
 *     "type": "CACHED"
 * }
 *
 * @author : john.peng created on date : 2017/10/18
 */
public class InValidateFunc implements ValidateFunc {
    public static final String CODE = "InValidate";
    public static final String NAME = "7. 被包含(存在性)检验";

    /**
     * {@inheritDoc}
     *
     * @see ValidateFunc#validate(RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data) {
        String category = validateConfig.getString("category");
        String columnName = column.getName();
        Object value = data.get(columnName);
        if (value != null) {
            if ("CACHED".equalsIgnoreCase(category)) {
                if (!JdbcManager.getManager().existInCache(columnName, value)) {
                    return new ValidateError(columnName, String.format("数据值[%s]在字典中不存在。", value.toString()));
                }
            } else if ("REAL".equalsIgnoreCase(category)) {
                String dataSource = validateConfig.getString("dataSource");
                String sql = validateConfig.getString("sql");
                if (!JdbcManager.getManager().existInReal(columnName, value, dataSource, sql)) {
                    return new ValidateError(columnName, String.format("数据值[%s]在字典中不存在，检查SQL[%s]。",
                            value.toString(), sql));
                }
            } else {
                return new ValidateError(column.getName(), String.format("不支持的存在性检验类型[%s]。", category));
            }
        }
        return null;
    }
}
