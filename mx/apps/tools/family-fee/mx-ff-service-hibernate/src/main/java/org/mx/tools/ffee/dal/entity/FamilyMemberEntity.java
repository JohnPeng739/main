package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 描述： 家庭成员信息实体类，基于Hibernate实现。
 *
 * @author: John.Peng
 * @date: 2018/2/18 下午7:52
 */
@Entity
@Table(name = "TB_FAMILY_MEMBER")
public class FamilyMemberEntity extends BaseEntity implements FamilyMember {
    @Column(name = "MEMBER_ROLE")
    private String memberRole;
    @ManyToOne(targetEntity = FfeeAccountEntity.class)
    private FfeeAccount ffeeAccount;

    /**
     * {@inheritDoc}
     *
     * @see FamilyMember#getMemberRole()
     */
    public String getMemberRole() {
        return memberRole;
    }

    /**
     * {@inheritDoc}
     *
     * @see FamilyMember#setMemberRole(String)
     */
    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    /**
     * {@inheritDoc}
     *
     * @see FamilyMember#getFfeeAccount()
     */
    public FfeeAccount getFfeeAccount() {
        return ffeeAccount;
    }

    /**
     * {@inheritDoc}
     *
     * @see FamilyMember#setFfeeAccount(FfeeAccount)
     */
    public void setFfeeAccount(FfeeAccount ffeeAccount) {
        this.ffeeAccount = ffeeAccount;
    }
}
