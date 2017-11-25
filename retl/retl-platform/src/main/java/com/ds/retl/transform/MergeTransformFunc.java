package com.ds.retl.transform;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;
import org.mx.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 字段合并转换规则类，将多个字段使用连接字符串进行连接合并。
 *
 * 配置信息：
 * {
 *     fields: 'code,name',
 *     separator: ','
 * }
 *
 * @author : john.peng created on date : 2017/9/8
 */
public class MergeTransformFunc implements TransformFunc {
    public static final String CODE = "MergeTransform";
    public static final String NAME = "1. 字段合并转换";

    /**
     * {@inheritDoc}
     *
     * @see TransformFunc#transform(Map, RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol,
                                          JSONObject config, JSONObject data) {
        JSONArray fields = config.getJSONArray("fields");
        String separator = config.getString("separator");
        if (StringUtils.isBlank(separator) && !" ".equals(separator)) {
            separator = ",";
        }
        StringBuffer sb = new StringBuffer();
        List<TransformError> errors = new ArrayList<>();
        for (int index = 0; index < fields.size(); index ++) {
            String field = fields.getString(index);
            if (data.keySet().contains(field)) {
                Object value = data.get(field);
                sb.append(String.valueOf(value));
                sb.append(separator);
            } else {
                RecordColumn col = columns.get(field);
                errors.add(new TransformError(currentCol.getName(),
                        String.format("字段[%s：%s]不存在，转换失败。", col.getName(), col.getDesc()), data));
            }
        }
        if (errors.isEmpty()) {
            if (sb.length() > 1) {
                sb.delete(sb.length() - separator.length(), sb.length());
            }
            data.put(currentCol.getName(), sb.toString());
            return null;
        } else {
            return errors;
        }
    }
}
