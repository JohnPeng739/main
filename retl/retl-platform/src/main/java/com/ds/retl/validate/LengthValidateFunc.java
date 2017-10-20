package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;

/**
 * 数据长度校验规则类
 *
 * @author : john.peng date : 2017/9/7
 */
public class LengthValidateFunc implements ValidateFunc {
    public static final String CODE = "LengthValidate";
    public static final String NAME = "3. 长度检验";

    /**
     * {@inheritDoc}
     *
     * @see ValidateFunc#validate(RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data) {
        if (!data.keySet().contains(column.getName())) {
            // 指定的字段不存在
            return new ValidateError(column.getName(), String.format("字段[%s：%s]不存在，长度校验失败。",
                    column.getName(), column.getDesc()), data);
        }
        Object value = data.get(column.getName());
        if (value != null) {
            if (value instanceof String) {
                String str = (String) value;
                int length = str.length();
                int min = validateConfig.getIntValue("min"), max = validateConfig.getIntValue("max");
                if (max > 0 && min > 0 && max < min) {
                    return new ValidateError(column.getName(), String.format("字段[%s：%s]校验规则中最大值[%d]小于最小值[%d]，长度校验失败。",
                            column.getName(), column.getDesc(), max, min), data);
                }
                if (max > 0 && length > max) {
                    return new ValidateError(column.getName(), String.format("字段[%s：%s]的长度大于%d，长度校验失败。",
                            column.getName(), column.getDesc(), max), data);
                }
                if (min > 0 && length < min) {
                    return new ValidateError(column.getName(), String.format("字段[%s：%s]的长度小于%d，长度校验失败。",
                            column.getName(), column.getDesc(), min), data);
                }
                return null;
            } else {
                return new ValidateError(column.getName(), String.format("字段[%s：%s]的值不是字符串类型，长度校验失败。",
                        column.getName(), column.getDesc()), data);
            }
        } else {
            return null;
        }
    }
}
