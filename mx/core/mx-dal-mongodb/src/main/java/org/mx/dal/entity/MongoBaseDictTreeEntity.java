package org.mx.dal.entity;

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

    /**
     * 设置节点的下级节点集合
     *
     * @param children 下级节点集合
     */
    public void setChildren(Set<T> children) {
        this.children = children;
    }
}
