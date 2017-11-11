package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 角色对象实体定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Entity
@Table(name = "TB_ROLE")
public class RoleEntity extends BaseDictEntity implements Role {
    @ManyToMany(targetEntity = AccountEntity.class)
    @JoinTable(name = "TB_ACCOUNT_ROLE",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID"))
    private List<Account> accounts;
    @ManyToMany(targetEntity = PrivilegeEntity.class)
    @JoinTable(name = "TB_ROLE_PRIVILEGE",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "ID"))
    private List<Privilege> privileges;

    /**
     * {@inheritDoc}
     * @see Role#getAccounts()
     */
    @Override
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * {@inheritDoc}
     * @see Role#getPrivileges()
     */
    @Override
    public List<Privilege> getPrivileges() {
        return privileges;
    }

    /**
     * {@inheritDoc}
     * @see Role#setAccounts(List)
     */
    @Override
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * {@inheritDoc}
     * @see Role#setPrivileges(List)
     */
    @Override
    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }
}
