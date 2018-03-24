package org.mx.kbm.entity;

import org.mx.dal.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 描述： 知识关系图谱实体类，基于Mongodb实现。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午8:18
 */
@Document(collection = "kgraph")
public class KGraphEntity extends BaseEntity implements KGraph {
    @DBRef
    private KNode src = null, tar = null;
    private NodeRelationship relationship = NodeRelationship.REFERENCE;

    /**
     * {@inheritDoc}
     *
     * @see KGraph#getSrc()
     */
    @Override
    public KNode getSrc() {
        return src;
    }

    /**
     * {@inheritDoc}
     *
     * @see KGraph#setSrc(KNode)
     */
    @Override
    public void setSrc(KNode src) {
        this.src = src;
    }

    /**
     * {@inheritDoc}
     *
     * @see KGraph#getTar()
     */
    @Override
    public KNode getTar() {
        return tar;
    }

    /**
     * {@inheritDoc}
     *
     * @see KGraph#setTar(KNode)
     */
    @Override
    public void setTar(KNode tar) {
        this.tar = tar;
    }

    /**
     * {@inheritDoc}
     *
     * @see KGraph#getRelationship()
     */
    @Override
    public NodeRelationship getRelationship() {
        return relationship;
    }

    /**
     * {@inheritDoc}
     *
     * @see KGraph#setRelationship(NodeRelationship)
     */
    @Override
    public void setRelationship(NodeRelationship relationship) {
        this.relationship = relationship;
    }
}
