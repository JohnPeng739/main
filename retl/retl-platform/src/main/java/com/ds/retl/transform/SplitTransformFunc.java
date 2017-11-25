package com.ds.retl.transform;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;
import org.mx.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 字段分解转换规则类。
 * 配置信息：
 * {
 *     fields: 'code,name',
 *     separator: ','
 * }
 *
 * @author : john.peng created on date : 2017/9/8
 * @see TransformFunc
 */
public class SplitTransformFunc implements TransformFunc {
    public static final String CODE = "SplitTransform";
    public static final String NAME = "3. 字段分割转换";

    /**
     * {@inheritDoc}
     * @see TransformFunc#transform(Map, RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol, JSONObject config, JSONObject data) {
        JSONArray fields = config.getJSONArray("fields");
        if (fields == null || fields.size() <= 0) {
            return Arrays.asList(new TransformError(currentCol.getName(), "转换目标字段列表为空。"));
        }
        String column = config.getString("field");
        String separator = config.getString("separator");
        if (StringUtils.isBlank(separator) && !" ".equals(separator)) {
            separator = ",";
        }
        String value = data.getString(column);
        if (!StringUtils.isBlank(value)) {
            String[] values = value.split(separator);
            if (values.length > fields.size()) {
                return Arrays.asList(new TransformError(column,
                        String.format("转换数据后需要%d个字段，但仅配置了%d个字段。", values.length, fields.size())));
            }
            if (values != null && values.length > 0) {
                for (int index = 0; index < Math.min(values.length, fields.size()); index++) {
                    data.put(fields.getString(index), values[index]);
                }
            }
        }
        return null;
    }
}
