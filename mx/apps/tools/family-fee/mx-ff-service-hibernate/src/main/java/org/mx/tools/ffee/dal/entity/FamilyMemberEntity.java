package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.*;

/**
 * 描述： 家庭成员信息实体类，基于Hibernate实现。
 *
 * @author John.Peng
 *         Date time 2018/2/18 下午7:52
 */
@Entity
@Table(name = "TB_FAMILY_MEMBER")
public class FamilyMemberEntity extends BaseEntity implements FamilyMember {
    @Column(name = "MEMBER_ROLE", length = 20)
    private String role;
    @OneToOne(targetEntity = FfeeAccountEntity.class)
    @JoinColumn(name = "ACCOUNT_ID")
    private FfeeAccount account;
    @Column(name = "IS_OWNER")
    private boolean isOwner;
    @ManyToOne(targetEntity = FamilyEntity.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "FAMILY_ID")
    private Family family;

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public FfeeAccount getAccount() {
        return account;
    }

    @Override
    public boolean isOwner() {
        return isOwner;
    }

    @Override
    public Family getFamily() {
        return family;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public void setAccount(FfeeAccount account) {
        this.account = account;
    }

    @Override
    public void setIsOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    public void setFamily(Family family) {
        this.family = family;
    }
}
