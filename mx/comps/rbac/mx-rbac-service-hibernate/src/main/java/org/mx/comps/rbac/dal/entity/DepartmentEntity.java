package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    // 只查看关系，不维护，在User中维护关系。
    @OneToMany(targetEntity = UserEntity.class, cascade = {CascadeType.REFRESH}, mappedBy = "department", fetch = FetchType.EAGER)
    private Set<User> employees;

    /**
     * 默认的构造函数
     */
    public DepartmentEntity() {
        super();
        employees = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see Department#addEmployee(User)
     */
    @Override
    public void addEmployee(User employee) {
        employee.setDepartment(this);
        employees.add(employee);
    }

    /**
     * {@inheritDoc}
     *
     * @see Department#removeEmployee(User)
     */
    @Override
    public void removeEmployee(User employee) {
        employee.setDepartment(null);
        employees.remove(employee);
    }

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
    public Set<User> getEmployees() {
        return employees;
    }

    /**
     * {@inheritDoc}
     *
     * @see Department#setEmployees(Set)
     */
    @Override
    public void setEmployees(Set<User> employees) {
        this.employees = employees;
    }
}
