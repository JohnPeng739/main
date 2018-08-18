package org.mx.dal.entity;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于Mongodb实现的基础树状字典实体
 *
 * @author : john.peng date : 2017/10/8
 * @see MongoBaseDictEntity
 * @see BaseDictTree
 */
public class MongoBaseDictTreeEntity<T extends BaseDictTree> extends MongoBaseDictEntity implements BaseDictTree {
    @Transient
    private T parent;
    private String parentId;
    @DBRef
    private Set<T> children = new HashSet<>();

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#getParentId()
     */
    @Override
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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
        this.parentId = parent.getId();
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
