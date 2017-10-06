package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;

/**
 * Created by john on 2017/9/7.
 */
public interface ValidateFunc {
    ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data);
}
