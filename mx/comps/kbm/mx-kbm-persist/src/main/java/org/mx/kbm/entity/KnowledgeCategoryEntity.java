package org.mx.kbm.entity;

import org.mx.dal.entity.BaseDictTreeEntity;

import javax.persistence.*;

/**
 * 描述： 基于Hibernate JPA实现的知识分类实体类
 *
 * @author John.Peng
 *         Date time 2018/4/27 下午3:41
 */
@Entity
@Table(name = "TB_KCATEGORY")
public class KnowledgeCategoryEntity extends BaseDictTreeEntity<KnowledgeCategoryEntity> implements KnowledgeCategory {
    @ManyToOne(targetEntity = KnowledgeTenantEntity.class, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "TENANT_ID")
    private KnowledgeTenant tenant;
    @Column(name = "ISPRIVATED")
    private boolean privated = true;

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeCategory#getTenant()
     */
    @Override
    public KnowledgeTenant getTenant() {
        return tenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeCategory#setTenant(KnowledgeTenant)
     */
    @Override
    public void setTenant(KnowledgeTenant knowledgeTenant) {
        this.tenant = knowledgeTenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeCategory#isPriviated()
     */
    @Override
    public boolean isPriviated() {
        return privated;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeCategory#setPrivated(boolean)
     */
    @Override
    public void setPrivated(boolean privated) {
        this.privated = privated;
    }
}
