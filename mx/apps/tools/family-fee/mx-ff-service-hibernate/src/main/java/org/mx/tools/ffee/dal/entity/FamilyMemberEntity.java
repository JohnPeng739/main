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
    private FfeeAccount ffeeAccount;
    @Column(name = "IS_OWNER")
    private boolean isOwner;

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public FfeeAccount getFfeeAccount() {
        return ffeeAccount;
    }

    @Override
    public boolean isOwner() {
        return isOwner;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public void setFfeeAccount(FfeeAccount ffeeAccount) {
        this.ffeeAccount = ffeeAccount;
    }

    @Override
    public void setIsOwner(boolean owner) {
        isOwner = owner;
    }
}
