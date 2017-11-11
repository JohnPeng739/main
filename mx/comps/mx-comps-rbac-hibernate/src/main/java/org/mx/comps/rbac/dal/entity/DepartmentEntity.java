package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 部门对象实体定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Entity
@Table(name = "TB_DEPARTMENT")
public class DepartmentEntity extends BaseDictEntity implements Department {
    @ManyToOne(targetEntity = UserEntity.class)
    private User manager;
    @ManyToMany(targetEntity = UserEntity.class)
    @JoinTable(name = "TB_EMPLOYEES",
            joinColumns = @JoinColumn(name = "DEPARTMENT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"))
    private List<User> employees;

    /**
     * {@inheritDoc}
     *
     * @see Department#getManager()
     */
    @Override
    public User getManager() {
        return manager;
    }

    /**
     * {@inheritDoc}
     *
     * @see Department#getManager()
     */
    @Override
    public void setManager(User manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     *
     * @see Department#getEmployees()
     */
    @Override
    public List<User> getEmployees() {
        return employees;
    }

    /**
     * {@inheritDoc}
     *
     * @see Department#setEmployees(List)
     */
    @Override
    public void setEmployees(List<User> employees) {
        this.employees = employees;
    }
}
