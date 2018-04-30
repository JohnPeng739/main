package org.mx.dal.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * 基于Hibernate实现的基础树状字典实体
 *
 * @author : john.peng date : 2017/10/6
 * @see BaseDictEntity
 * @see BaseDictTree
 */
@MappedSuperclass
public class BaseDictTreeEntity<T extends BaseDictTree> extends BaseDictEntity implements BaseDictTree {
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "PARENT_ID")
    private T parent;
    @OneToMany(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "PARENT_ID")
    private Set<T> children;

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
    public BaseDictTree getParent() {
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
    public Set<? extends BaseDictTree> getChildren() {
        return children;
    }
}
