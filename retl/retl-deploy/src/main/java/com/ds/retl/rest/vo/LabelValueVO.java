package com.ds.retl.rest.vo;

/**
 * Created by john on 2017/10/6.
 */
public class LabelValueVO {
    String label, value;

    public LabelValueVO() {
        super();
    }

    public LabelValueVO(String label, String value) {
        this();
        this.label = label;
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
