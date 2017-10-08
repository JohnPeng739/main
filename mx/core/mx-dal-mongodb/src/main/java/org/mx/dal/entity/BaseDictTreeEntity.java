package org.mx.dal.entity;

/**
 * Created by john on 2017/10/8.
 */
public class BaseDictTreeEntity extends BaseDictEntity implements BaseDictTree {
    private String parentId;

    @Override
    public String toString() {
        return super.toString() +
                ", parentId='" + parentId + '\'';
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getParentId() {
        return parentId;
    }
}