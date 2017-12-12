package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 账户对象实体定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Document(collection = "account")
public class AccountEntity extends BaseDictEntity implements Account {
    private String password;
    @DBRef
    private User owner;
    @DBRef
    private Set<Role> roles;

    /**
     * 默认的构造函数
     */
    public AccountEntity() {
        super();
        roles = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see Account#getPassword()
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     *
     * @see Account#setPassword(String)
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * {@inheritDoc}
     *
     * @see Account#getOwner()
     */
    @Override
    public User getOwner() {
        return owner;
    }

    /**
     * {@inheritDoc}
     *
     * @see Account#setOwner(User)
     */
    @Override
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * {@inheritDoc}
     *
     * @see Account#getRoles()
     */
    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * {@inheritDoc}
     *
     * @see Account#setRoles(Set)
     */
    @Override
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
