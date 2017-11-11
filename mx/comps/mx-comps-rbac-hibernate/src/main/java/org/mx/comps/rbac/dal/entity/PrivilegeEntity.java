package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 特权对象实体定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Entity
@Table(name = "TB_PRIVILEGE")
public class PrivilegeEntity extends BaseDictEntity implements Privilege {
    @ManyToMany(targetEntity = RoleEntity.class)
    @JoinTable(name = "TB_ROLE_PRIVILEGE",
            joinColumns = @JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
    private List<Role> roles;

    /**
     * {@inheritDoc}
     * @see Privilege#getRoles()
     */
    @Override
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * {@inheritDoc}
     * @see Privilege#setRoles(List)
     */
    @Override
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
