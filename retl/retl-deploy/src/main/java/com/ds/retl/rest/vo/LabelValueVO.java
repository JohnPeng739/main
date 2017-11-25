package com.ds.retl.rest.vo;

import org.mx.StringUtils;

/**
 * Label-Value格式的值对象类定义
 *
 * @author : john.peng created on date : 2017/10/6
 */
public class LabelValueVO implements Comparable<LabelValueVO> {
    String label, value;

    /**
     * 默认的构造函数
     */
    public LabelValueVO() {
        super();
    }

    /**
     * 构造函数
     *
     * @param label 标签
     * @param value 值
     */
    public LabelValueVO(String label, String value) {
        this();
        this.label = label;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(LabelValueVO tar) {
        if (tar == null && StringUtils.isBlank(this.label) && StringUtils.isBlank(tar.getLabel())) {
            return 0;
        }
        if (!StringUtils.isBlank(this.label)) {
            return this.label.compareTo(tar.getLabel());
        } else {
            return tar.getLabel().compareTo(this.label) * -1;
        }
    }

    /**
     * 获取标签
     *
     * @return 标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 设置标签
     *
     * @param label 标签
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 获取值
     *
     * @return 值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(String value) {
        this.value = value;
    }
}
