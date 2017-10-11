package com.ds.retl.transform;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;
import org.mx.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 2017/9/8.
 */
public class MergeTransformFunc implements TransformFunc {
    public static final String CODE = "MergeTransform";
    public static final String NAME = "1. 字段合并转换";

    @Override
    public List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol,
                                          JSONObject config, JSONObject data) {
        String fieldsStr = config.getString("fields");
        String separator = config.getString("separator");
        if (StringUtils.isBlank(separator) && !" ".equals(separator)) {
            separator = ",";
        }
        String[] fields = fieldsStr.split(",");
        StringBuffer sb = new StringBuffer();
        List<TransformError> errors = new ArrayList<>();
        for (String field : fields) {
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
