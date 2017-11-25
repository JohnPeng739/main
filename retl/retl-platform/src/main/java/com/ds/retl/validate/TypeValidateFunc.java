package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据类型校验规则类
 *
 * @author : john date : 2017/9/8
*/
public class TypeValidateFunc implements ValidateFunc {
    public static final String CODE = "TypeValidate";
    public static final String NAME = "2. 类型检验";

    public enum ValueType {STRING, DATE, INT, DECIMAL, BOOL};

    /**
     * {@inheritDoc}
     * @see ValidateFunc#validate(RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data) {
        if (!data.keySet().contains(column.getName())) {
            // 指定的字段不存在
            // update by lichunliang 数据类型校验时如果字段不存在,不返回错误，跳过校验 20171117
            return null;
            /*return new ValidateError(column.getName(), String.format("字段[%s：%s]不存在，值类型校验失败。",
                    column.getName(), column.getDesc()), data);*/
        }
        String type = validateConfig.getString("valueType");
        ValueType valueType = ValueType.valueOf(type);
        Object value = data.get(column.getName());
        if (value != null) {
            switch (valueType) {
                case STRING:
                    if (value instanceof String) {
                        return null;
                    }
                    break;
                case DATE:
                    if (value instanceof Long) {
                        return null;
                    }
                    break;
                case INT:
                    if (value instanceof Integer || value instanceof Long) {
                        return null;
                    }
                    break;
                case DECIMAL:
                    if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
                        return null;
                    }
                    break;
                case BOOL:
                    if (value instanceof Boolean) {
                        return null;
                    }
                    break;
                default:
                    return new ValidateError(column.getName(),
                            String.format("字段[%s：%s]的值类型[%s]不被支持，值类型校验失败。",
                                    column.getName(), column.getDesc(), valueType.name()), data);
            }
            return new ValidateError(column.getName(),
                    String.format("字段[%s：%s]的值类型[%s]不是[%s]类型，值类型校验失败。",
                            column.getName(), column.getDesc(), value.getClass().getSimpleName(), valueType), data);
        } else {
            return null;
        }
    }
}
