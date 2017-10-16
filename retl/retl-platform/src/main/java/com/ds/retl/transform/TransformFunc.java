package com.ds.retl.transform;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;

import java.util.List;
import java.util.Map;

/**
 * 数据转换规则接口定义
 *
 * @author : john.peng created on date : 2017/9/7
 */
public interface TransformFunc {
    /**
     * 数据转换接口方法
     *
     * @param columns    数据列定义
     * @param currentCol 转换结果列对象
     * @param config     转换配置信息
     * @param data       数据对象
     * @return 如果转换成功，返回null；否则返回转换错误列表。
     */
    List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol, JSONObject config,
                                   JSONObject data);
}
