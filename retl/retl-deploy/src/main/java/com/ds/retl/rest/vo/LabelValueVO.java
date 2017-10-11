package com.ds.retl.rest.vo;

import org.mx.StringUtils;

/**
 * Created by john on 2017/10/6.
 */
public class LabelValueVO implements Comparable<LabelValueVO> {
    String label, value;

    public LabelValueVO() {
        super();
    }

    public LabelValueVO(String label, String value) {
        this();
        this.label = label;
        this.value = value;
    }

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
