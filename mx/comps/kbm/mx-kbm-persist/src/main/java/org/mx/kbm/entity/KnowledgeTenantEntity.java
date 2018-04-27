package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.AccountEntity;
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
    @OneToMany
    private Set<Account> member; // 成员列表
    @Column(name = "TENANT_TYPE")
    private TenantType type; // 类型
    @ManyToOne(targetEntity = AccountEntity.class)
    private Account contact; // 联系人

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#getMember()
     */
    @Override
    public Set<Account> getMember() {
        return member;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#setMember(Set)
     */
    @Override
    public void setMember(Set<Account> member) {
        this.member = member;
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
    public Account getContact() {
        return contact;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeTenant#setContact(Account)
     */
    @Override
    public void setContact(Account contact) {
        this.contact = contact;
    }
}
