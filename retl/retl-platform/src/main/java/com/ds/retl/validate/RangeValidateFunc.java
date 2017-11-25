package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;

import java.math.BigDecimal;

/**
 * Created by john on .
 */

/**
 * 数据范围校验规则类
 *
 * @author : john.peng date : 2017/9/19
 */
public class RangeValidateFunc implements ValidateFunc {
    public static final String CODE = "RangeValidate";
    public static final String NAME = "4. 数据范围检验";

    /**
     * {@inheritDoc}
     *
     * @see ValidateFunc#validate(RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data) {
        if (!data.keySet().contains(column.getName())) {
            // 指定的字段不存在
            // update by lichunliang 数据范围校验时如果字段不存在,不返回错误，跳过校验 20171117
            return null;
            /*return new ValidateError(column.getName(), String.format("字段[%s：%s]不存在，范围校验失败。",
                    column.getName(), column.getDesc()), data);*/
        }
        Object value = data.get(column.getName());
        if (value != null) {
            if (value instanceof Integer || value instanceof Short || value instanceof Long || value instanceof Float ||
                    value instanceof Double || value instanceof BigDecimal) {
                double v = Double.valueOf(value.toString());
                int min = validateConfig.getIntValue("min"), max = validateConfig.getIntValue("max");
                if (max > 0 && min > 0 && max < min) {
                    return new ValidateError(column.getName(), String.format("字段[%s：%s]校验规则中最大值[%d]小于最小值[%d]，范围校验失败。",
                            column.getName(), column.getDesc(), max, min), data);
                }
                if (max > 0 && v > max) {
                    return new ValidateError(column.getName(), String.format("字段[%s：%s]的值[%f]大于%d，范围校验失败。",
                            column.getName(), column.getDesc(), v, max), data);
                }
                if (min > 0 && v < min) {
                    return new ValidateError(column.getName(), String.format("字段[%s：%s]的值[%f]小于%d，范围校验失败。",
                            column.getName(), column.getDesc(), v, min), data);
                }
                return null;
            } else {
                return new ValidateError(column.getName(), String.format("字段[%s：%s]的值不是数值类型，范围校验失败。",
                        column.getName(), column.getDesc()), data);
            }
        } else {
            return null;
        }

    }
}
