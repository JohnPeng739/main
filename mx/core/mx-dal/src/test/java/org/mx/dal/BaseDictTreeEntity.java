package org.mx.dal;

import org.mx.dal.entity.BaseDictTree;

public class BaseDictTreeEntity extends BaseDictEntity implements BaseDictTree {
    private String parentId;

    /**
     * {@inheritDoc}
     *
     * @see BaseDictEntity#toString()
     */
    @Override
    public String toString() {
        return super.toString() +
                ", parentId='" + parentId + '\'';
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#getParentId()
     */
    @Override
    public String getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#setParentId(String)
     */
    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
