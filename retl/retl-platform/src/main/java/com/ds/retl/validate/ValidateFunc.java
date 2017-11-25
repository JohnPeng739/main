package com.ds.retl.validate;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.ValidateError;

/**
 * 校验规则接口定义
 *
 * @author : john.peng date : 2017/9/7
 */
public interface ValidateFunc {
    /**
     * 校验方法
     *
     * @param column         待校验列定义
     * @param validateConfig 校验依赖的配置信息
     * @param data           数据，已JSONObject方式存在
     * @return 如果校验通过，返回null；否则返回校验错误。
     */
    ValidateError validate(RecordColumn column, JSONObject validateConfig, JSONObject data);
}
