package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;

/**
 * Created by john on 2017/9/7.
 */
public class NullValidateFunc implements ValidateFunc {
    public static final String CODE = "NullValidate";
    public static final String NAME = "1. 空值检验";

    @Override
    public ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data) {
        boolean nullable = validateConfig.getBoolean("nullable");
        if (!data.keySet().contains(column.getName())) {
            // 指定的字段不存在
            return new ValidateError(column.getName(), String.format("字段[%s：%s]不存在，空值校验失败。",
                    column.getName(), column.getDesc()), data);
        }
        Object value = data.get(column.getName());
        if (value == null && !nullable) {
            // 不能为空却null了
            return new ValidateError(column.getName(), String.format("字段[%s：%s]为空，空值校验失败。",
                    column.getName(), column.getDesc()), data);
        } else {
            return null;
        }
    }
}
