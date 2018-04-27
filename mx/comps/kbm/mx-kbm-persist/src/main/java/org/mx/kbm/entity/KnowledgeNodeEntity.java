package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.AccountEntity;
import org.mx.dal.entity.BaseEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * 描述： 基于Hibernate JPA实现的知识条目实体类
 *
 * @author John.Peng
 *         Date time 2018/4/27 下午3:49
 */
@Entity
@Table(name = "TB_KNODE", indexes = {
        @Index(name = "IDX_KNODE_TYPE", columnList = "type")
})
public class KnowledgeNodeEntity extends BaseEntity implements KnowledgeNode {
    @Column(name = "ATTACHMENT_TYPE")
    private AttachmentType type;
    @Column(name = "ATTACHMENT_LEN")
    private long length;
    @Column(name = "ATTACHMENT_URI")
    private String uri;
    @Column(name = "LAST_VERSION")
    private String lastVersion;
    @Column(name = "SUMMARY")
    private String summary;
    @Column(name = "KEYWORDS")
    private String keywords;
    @ManyToOne(targetEntity = KnowledgeTenantEntity.class)
    private KnowledgeTenant tenant;
    @ManyToOne(targetEntity = KnowledgeCategoryEntity.class)
    private KnowledgeCategory category;
    @ManyToOne(targetEntity = AccountEntity.class)
    private Account owner;
    @OneToMany(targetEntity = KnowledgeNodeEntity.class, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "REFERENCE_ID")
    private Set<KnowledgeNode> references;

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getType()
     */
    @Override
    public AttachmentType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setType(AttachmentType)
     */
    @Override
    public void setType(AttachmentType type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getLength()
     */
    @Override
    public long getLength() {
        return length;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setLength(long)
     */
    @Override
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getUri()
     */
    @Override
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setUri(String)
     */
    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getLastVersion()
     */
    @Override
    public String getLastVersion() {
        return lastVersion;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setLastVersion(String)
     */
    @Override
    public void setLastVersion(String version) {
        this.lastVersion = version;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getKeywords()
     */
    @Override
    public String getKeywords() {
        return keywords;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setKeywords(String)
     */
    @Override
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getSummary()
     */
    @Override
    public String getSummary() {
        return summary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setSummary(String)
     */
    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getTenant()
     */
    @Override
    public KnowledgeTenant getTenant() {
        return tenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setTenant(KnowledgeTenant)
     */
    @Override
    public void setTenant(KnowledgeTenant knowledgeTenant) {
        this.tenant = knowledgeTenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getCategory()
     */
    @Override
    public KnowledgeCategory getCategory() {
        return category;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setCategory(KnowledgeCategory)
     */
    @Override
    public void setCategory(KnowledgeCategory knowledgeCategory) {
        this.category = knowledgeCategory;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getOwner()
     */
    @Override
    public Account getOwner() {
        return owner;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setOwner(Account)
     */
    @Override
    public void setOwner(Account owner) {
        this.owner = owner;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#getReferences()
     */
    @Override
    public Set<KnowledgeNode> getReferences() {
        return references;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeNode#setReferences(Set)
     */
    @Override
    public void setReferences(Set<KnowledgeNode> references) {
        this.references = references;
    }
}
