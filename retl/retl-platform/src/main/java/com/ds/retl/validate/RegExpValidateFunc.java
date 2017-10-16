package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by john on .
 */

/**
 * 正则表达式校验规则类
 *
 * @author : john.peng date : 2017/9/8
 */
public class RegExpValidateFunc implements ValidateFunc {
    public static final String CODE = "RegExpValidate";
    public static final String NAME = "5. 正则检验";

    /**
     * {@inheritDoc}
     *
     * @see ValidateFunc#validate(RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data) {
        if (!data.keySet().contains(column.getName())) {
            // 指定的字段不存在
            return new ValidateError(column.getName(), String.format("字段[%s：%s]不存在，正则表达式校验失败。",
                    column.getName(), column.getDesc()), data);
        }
        Object value = data.get(column.getName());
        String regexp = validateConfig.getString("regexp");
        if (value instanceof String) {
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher((String) value);
            if (matcher.matches()) {
                return null;
            } else {
                return new ValidateError(column.getName(),
                        String.format("字段[%s：%s]的值不符合正则表达式[%s]要求，正则表达式校验失败。",
                                column.getName(), column.getDesc(), regexp), data);
            }
        } else {
            return new ValidateError(column.getName(),
                    String.format("字段[%s：%s]的值类型[%s]不是字符串类型，正则表达式校验失败。",
                            column.getName(), column.getDesc(), value.getClass().getSimpleName()), data);
        }
    }
}
