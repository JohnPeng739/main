package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色对象实体定义，使用JPA
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Entity
@Table(name = "TB_ROLE")
public class RoleEntity extends BaseDictEntity implements Role {
    @ManyToMany(targetEntity = AccountEntity.class, fetch = FetchType.LAZY)
    @JoinTable(name = "TB_ACCOUNT_ROLE",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID"))
    private Set<Account> accounts;
    @ManyToMany(targetEntity = PrivilegeEntity.class, fetch = FetchType.LAZY)
    @JoinTable(name = "TB_ROLE_PRIVILEGE",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "ID"))
    private Set<Privilege> privileges;

    /**
     * 默认的构造函数
     */
    public RoleEntity() {
        super();
        accounts = new HashSet<>();
        privileges = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     * @see Role#getAccounts()
     */
    @Override
    public Set<Account> getAccounts() {
        return accounts;
    }

    /**
     * {@inheritDoc}
     * @see Role#getPrivileges()
     */
    @Override
    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    /**
     * {@inheritDoc}
     * @see Role#setAccounts(Set)
     */
    @Override
    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * {@inheritDoc}
     * @see Role#setPrivileges(Set)
     */
    @Override
    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }
}
