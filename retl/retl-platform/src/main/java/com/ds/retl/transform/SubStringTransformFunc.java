package com.ds.retl.transform;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;
import org.mx.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 截取字符串子串的转换规格定义
 * <p>
 * 配置：
 * {
 * field: 'name',
 * startPos: 0,
 * endPos: 20
 * }
 *
 * @author : john.peng created on date : 2017/11/8
 */
public class SubStringTransformFunc implements TransformFunc {
    public static final String CODE = "SubStringTransform";
    public static final String NAME = "6. 字符串子串转换";

    /**
     * {@inheritDoc}
     *
     * @see TransformFunc#transform(Map, RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol,
                                          JSONObject config, JSONObject data) {
        String fieldCode = config.getString("field");
        if (StringUtils.isBlank(fieldCode)) {
            return Arrays.asList(new TransformError(currentCol.getName(), "指定的列名为空。", data));
        }
        String value = data.getString(fieldCode);
        if (StringUtils.isBlank(value)) {
            data.put(currentCol.getName(), "");
            return null;
        }
        int startPos = config.getIntValue("startPos"),
                endPos = config.getIntValue("endPos");
        if (startPos < 0) {
            startPos = 0;
        }
        if (endPos <= 0 || endPos >= value.length()) {
            endPos = value.length() - 1;
        }
        String subString = value.substring(startPos, endPos);
        data.put(currentCol.getName(), subString);
        return null;
    }
}
