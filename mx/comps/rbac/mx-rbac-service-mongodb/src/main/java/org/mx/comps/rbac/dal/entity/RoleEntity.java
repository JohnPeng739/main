package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色对象实体定义，使用MongoDB
 *
 * @author : john.peng created on date : 2017/12/12
 */
@Document(collection = "role")
public class RoleEntity extends BaseDictEntity implements Role {
    @DBRef
    private Set<Account> accounts;
    @DBRef
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
