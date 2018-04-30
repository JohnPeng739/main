package org.mx.dal.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于Mongodb实现的基础树状字典实体
 *
 * @author : john.peng date : 2017/10/8
 * @see BaseDictEntity
 * @see BaseDictTree
 */
public class BaseDictTreeEntity<T extends BaseDictTree> extends BaseDictEntity implements BaseDictTree {
    @DBRef
    private T parent;
    @DBRef
    private Set<T> children = new HashSet<>();

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

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#getParent()
     */
    @Override
    public T getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#setParent(BaseDictTree)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setParent(BaseDictTree parent) {
        this.parent = (T) parent;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#getChildren()
     */
    @Override
    public Set<T> getChildren() {
        return children;
    }
}
