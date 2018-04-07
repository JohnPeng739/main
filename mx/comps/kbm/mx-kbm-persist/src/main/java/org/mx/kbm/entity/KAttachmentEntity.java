package org.mx.kbm.entity;

import org.mx.dal.entity.BaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 描述： 知识附件实体类，基于Mongodb实现。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午8:11
 */
@Document(collection = "kattachment")
public class KAttachmentEntity extends BaseEntity implements KAttachment {
    @DBRef
    private KNode kNode = null;
    @Indexed
    private AttachmentType type = AttachmentType.TXT;
    private String uri = null;
    @TextIndexed
    private String name, summary = null;
    @DBRef
    private Tenant tenant = null;

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#getKNode()
     */
    @Override
    public KNode getKNode() {
        return kNode;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#setKNode(KNode)
     */
    @Override
    public void setKNode(KNode kNode) {
        this.kNode = kNode;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#getType()
     */
    @Override
    public AttachmentType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#setType(AttachmentType)
     */
    @Override
    public void setType(AttachmentType type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#getUri()
     */
    @Override
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#setUri(String)
     */
    @Override
    public void setUri(String urli) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#setName(String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#getSummary()
     */
    public String getSummary() {
        return summary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#setSummary(String)
     */
    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#getTenant()
     */
    @Override
    public Tenant getTenant() {
        return tenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KAttachment#setTenant(Tenant)
     */
    @Override
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
