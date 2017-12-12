package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 特权对象实体定义，使用JPA
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Entity
@Table(name = "TB_PRIVILEGE")
public class PrivilegeEntity extends BaseDictEntity implements Privilege {
    @ManyToMany(targetEntity = RoleEntity.class, fetch = FetchType.EAGER)
    @JoinTable(name = "TB_ROLE_PRIVILEGE",
            joinColumns = @JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
    private Set<Role> roles;

    /**
     * 默认的构造函数
     */
    public PrivilegeEntity() {
        super();
        roles = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see Privilege#getRoles()
     */
    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * {@inheritDoc}
     *
     * @see Privilege#setRoles(Set)
     */
    @Override
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
