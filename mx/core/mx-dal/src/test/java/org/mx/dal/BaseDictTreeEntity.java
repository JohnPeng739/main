package org.mx.dal;

import org.mx.dal.entity.BaseDictTree;

public abstract class BaseDictTreeEntity extends BaseDictEntity implements BaseDictTree {
    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#getParentId()
     */
    @Override
    public String getParentId() {
        BaseDictTree parent = getParent();
        return parent == null ? null : parent.getId();
    }
}
