package com.ds.retl.transform;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;

import java.util.List;
import java.util.Map;

/**
 * Created by john on 2017/9/7.
 */
public interface TransformFunc {
    List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol, JSONObject config,
                                   JSONObject data);
}
