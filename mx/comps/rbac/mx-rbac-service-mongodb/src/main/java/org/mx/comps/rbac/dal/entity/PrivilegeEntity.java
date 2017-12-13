package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 特权对象实体定义，使用MongoDB
 *
 * @author : john.peng created on date : 2017/12/12
 */
@Document(collection = "privilege")
public class PrivilegeEntity extends BaseDictEntity implements Privilege {
    // 由Role维护关系，这里延迟加载
    @DBRef(lazy = true)
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
