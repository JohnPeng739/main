package org.mx.dal.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by john on 2017/10/6.
 */
@MappedSuperclass
public class BaseDictTreeEntity extends BaseDictEntity implements BaseDictTree {
    @Column(name = "PARENT_ID", length = 40)
    private String parentId;

    @Override
    public String toString() {
        return super.toString() +
                ", parentId='" + parentId + '\'';
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
