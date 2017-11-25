package com.ds.retl.transform;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.RecordColumn;
import com.ds.retl.error.TransformError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 四则运算转换规则类，用于数值类型字段之间的简单四则运算操作，支持：加、减、乘、除和圆括号等运算。
 *
 * @author : john.peng created on date : 2017/9/7
 * @see TransformFunc
 */
public class FormulaTransformFunc implements TransformFunc {
    public static final String CODE = "FormulaTransform";
    public static final String NAME = "2. 四则运算转换";
    private static final Log logger = LogFactory.getLog(FormulaTransformFunc.class);

    private Map<String, RecordColumn> columns = null;
    private RecordColumn currentCol = null;

    private String formula = null;
    private Map<String, Double> values = null;
    private List<TransformError> errors = null;

    /**
     * {@inheritDoc}
     *
     * @see TransformFunc#transform(Map, RecordColumn, JSONObject, JSONObject)
     */
    @Override
    public List<TransformError> transform(Map<String, RecordColumn> columns, RecordColumn currentCol,
                                          JSONObject config, JSONObject data) {
        this.columns = columns;
        this.currentCol = currentCol;

        this.errors = new ArrayList<>();
        this.values = new HashMap<>();

        String formula = (String) config.get("calculate");
        this.formula = formula;
        checkFields(formula, data);
        if (this.errors.isEmpty()) {
            // 检查没有错误，进行计算
            double value = calculate(formula);
            data.put(this.currentCol.getName(), value);
            return null;
        } else {
            // 检查有错误，返回错误
            return this.errors;
        }
    }

    /**
     * 检测相关的字段，如果检测不通过，会抛出异常。
     *
     * @param formula 四则运算的公式
     * @param data    数据对象
     */
    private void checkFields(String formula, JSONObject data) {
        if (StringUtils.isBlank(formula)) {
            this.errors.add(new TransformError(currentCol.getName(),
                    String.format("转换公式[%s]格式不正确，转换失败。", this.formula)));
            return;
        }
        formula = formula.trim();
        int index1 = formula.indexOf("+"), index2 = formula.indexOf("-"), index3 = formula.indexOf("*"),
                index4 = formula.indexOf("/"), index5 = formula.indexOf("(");
        if (index1 == -1 && index2 == -1 && index3 == -1 && index4 == -1) {
            if (!data.keySet().contains(formula)) {
                RecordColumn col = this.columns.get(formula);
                this.errors.add(new TransformError(currentCol.getName(),
                        String.format("字段[%s：%s]不存在，转换失败。", col.getName(), col.getDesc())));
                return;
            } else {
                // 检查字段的值并缓存
                Object value = data.get(formula);
                if (value instanceof Integer || value instanceof Long || value instanceof Float ||
                        value instanceof Double || value instanceof BigDecimal) {
                    values.put(formula, Double.valueOf(value.toString()));
                } else if (value instanceof String) {
                    try {
                        values.put(formula, Double.parseDouble((String) value));
                    } catch (Exception ex) {
                        RecordColumn col = this.columns.get(formula);
                        this.errors.add(new TransformError(formula,
                                String.format("字段[%s：%s]的值[%s]不是数值类型，转换失败。",
                                        col.getName(), col.getDesc(), (String) value)));
                    }
                }
            }
            return;
        }
        if (index5 != -1) {
            // 有括号
            int index6 = formula.lastIndexOf(")");
            if (index6 == -1) {
                this.errors.add(new TransformError(currentCol.getName(),
                        String.format("转换公式[%s]格式不正确，转换失败。", this.formula)));
                return;
            } else {
                if (index5 > 1) {
                    checkFields(formula.substring(0, index5), data);
                }
                checkFields(formula.substring(index5 + 1, index6), data);
                if (index6 < formula.length() - 1) {
                    checkFields(formula.substring(index6 + 1), data);
                }
            }
        } else if (index3 != -1) {
            checkFields(formula, index3, data);
        } else if (index4 != -1) {
            checkFields(formula, index4, data);
        } else if (index1 != -1) {
            checkFields(formula, index1, data);
        } else if (index2 != -1) {
            checkFields(formula, index2, data);
        }
    }

    /**
     * 检查公式中的某一段字段
     *
     * @param formula 四则运算的公式
     * @param index   索引号
     * @param data    数据对象
     */
    private void checkFields(String formula, int index, JSONObject data) {
        if (index >= 1) {
            checkFields(formula.substring(0, index), data);
        }
        if (index <= formula.length() - 1) {
            checkFields(formula.substring(index + 1), data);
        }
    }

    /**
     * 计算公式的值
     *
     * @param formula 四则运算的公式
     * @return 计算结果值
     */
    private double calculate(String formula) {
        if (StringUtils.isBlank(formula)) {
            return 0.0;
        }
        formula = formula.trim();
        int index1 = formula.indexOf("+"), index2 = formula.indexOf("-"), index3 = formula.indexOf("*"),
                index4 = formula.indexOf("/"), index5 = formula.indexOf("(");
        if (index1 == -1 && index2 == -1 && index3 == -1 && index4 == -1) {
            return this.values.get(formula);
        }
        if (index5 != -1) {
            // 有括号
            int index6 = formula.lastIndexOf(")");
            double value = calculate(formula.substring(index5 + 1, index6));
            String s = formula.replace(formula.substring(index5, index6 + 1), String.valueOf(value));
            return calculate(s);
        } else if (index3 != -1) {
            return calculate(formula.substring(0, index3)) * calculate(formula.substring(index3 + 1));
        } else if (index4 != -1) {
            return calculate(formula.substring(0, index4)) / calculate(formula.substring(index4 + 1));
        } else if (index1 != -1) {
            return calculate(formula.substring(0, index1)) + calculate(formula.substring(index1 + 1));
        } else if (index2 != -1) {
            return calculate(formula.substring(0, index2)) - calculate(formula.substring(index2 + 1));
        } else {
            return Double.parseDouble(formula);
        }
    }
}
