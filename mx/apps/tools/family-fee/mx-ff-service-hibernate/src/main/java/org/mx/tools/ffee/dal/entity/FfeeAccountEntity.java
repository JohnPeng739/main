package org.mx.tools.ffee.dal.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.AccountEntity;
import org.mx.dal.entity.BaseEntity;

import javax.persistence.*;

/**
 * 描述： FFEE应用中的账户实体类，基于Hibernate实现，依赖RBAC中的Account。
 *
 * @author: John.Peng
 * @date: 2018/2/18 上午10:29
 */
@Entity
@Table(name = "TB_FFEE_ACCOUNT")
public class FfeeAccountEntity extends BaseEntity implements FfeeAccount {
    @OneToOne(targetEntity = AccountEntity.class)
    @PrimaryKeyJoinColumn
    private Account account;
    @Column(name = "SOURCE_TYPE")
    private AccountSourceType sourceType;

    /**
     * {@inheritDoc}
     *
     * @see FfeeAccount#getAccount()
     */
    public Account getAccount() {
        return account;
    }

    /**
     * {@inheritDoc}
     *
     * @see FfeeAccount#setAccount(Account)
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * {@inheritDoc}
     *
     * @see FfeeAccount#getSourceType()
     */
    public AccountSourceType getSourceType() {
        return sourceType;
    }

    /**
     * {@inheritDoc}
     *
     * @see FfeeAccount#setSourceType(AccountSourceType)
     */
    public void setSourceType(AccountSourceType sourceType) {
        this.sourceType = sourceType;
    }
}
