package org.mx.kbm.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * 描述： 基于Hibernate JPA实现的知识租户实体类
 *
 * @author John.Peng
 *         Date time 2018/4/27 下午3:23
 */
@Entity
@Table(name = "TB_KTENTANT", indexes = {
        @Index(name = "IDX_KTENANT_TYPE", columnList = "type")
})
public class KnowledgeTenantEntity extends BaseDictEntity implements KnowledgeTenant {
    @ManyToMany(targetEntity = KnowledgeContactEntity.class, cascade = {CascadeType.REFRESH})
    private Set<KnowledgeContact> members; // 成员列表
    @Column(name = "TENANT_TYPE")
    private TenantType type; // KnowledgeContact
    @ManyToOne(targetEntity = KnowledgeContact.class)
    private KnowledgeContact contact; // 联系人

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#getMembers()
     */
    @Override
    public Set<KnowledgeContact> getMembers() {
        return members;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#setMembers(Set)
     */
    @Override
    public void setMembers(Set<KnowledgeContact> members) {
        this.members = members;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#getType()
     */
    @Override
    public TenantType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#setType(TenantType)
     */
    @Override
    public void setType(TenantType tenantType) {
        this.type = tenantType;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#getContact()
     */
    @Override
    public KnowledgeContact getContact() {
        return contact;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#setContact(KnowledgeContact)
     */
    @Override
    public void setContact(KnowledgeContact contact) {
        this.contact = contact;
    }
}
