package com.ds.retl.transform;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;
import com.ds.retl.jdbc.JdbcManager;
import org.mx.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 对照表转换
 *
 * @author 李春亮
 * @version 3.0
 * @since 3.0
 */
public class ContrastTransformFunc implements TransformFunc{
    public static final String CODE = "ContrastTransform";
    public static final String NAME = "7. 对照表转换";

    @Override
    public List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol, JSONObject config, JSONObject data) {
        String srcColumn = config.getString("srcColumn");
        String columnName = currentCol.getName();
        if (StringUtils.isBlank(columnName)) {
            return Arrays.asList(new TransformError(currentCol.getName(), "字段名称为空。", data));
        }

        if (StringUtils.isBlank(srcColumn)) {
            return Arrays.asList(new TransformError(currentCol.getName(), "对源字段名称为空。", data));
        }

        String[] srcColArr = srcColumn.split(",");
        String srcValue = "";
        for (int i = 0; i < srcColArr.length; i++) {
            srcValue += ","+data.getString(srcColArr[i]);
        }
        srcValue = srcValue.substring(1);

        String tarValue =JdbcManager.getManager().getContrastValue(columnName, srcValue);
        data.put(columnName,tarValue);

        return null;
    }





}
