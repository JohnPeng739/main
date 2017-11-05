package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 用户对象实体定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Entity
@Table(name = "TB_USER")
public class UserEntity extends BaseEntity implements User {
    @Column(name = "FIRST_NAME", nullable = false, length = 20)
    private String firstName;
    @Column(name = "MIDDLE_NAME", length = 6)
    private String middleName;
    @Column(name = "LAST_NAME", nullable = false, length = 6)
    private String lastName;
    @Column(name = "DESCRIPTION", length = 255)
    private String desc;
    @Column(name = "SEX")
    private Sex sex;
    @Column(name = "BIRTHDAY")
    private Date birthday;
    @ManyToOne(targetEntity = DepartmentEntity.class)
    private Department department;
    @ManyToMany(targetEntity = UserEntity.class)
    @JoinTable(name = "TB_SUBORDINATES",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "SUBORDINATE_ID", referencedColumnName = "ID"))
    private List<User> subordinates;
    @Column(name = "STATION", length = 100)
    private String station;

    /**
     * {@inheritDoc}
     *
     * @see User#getFirstName()
     */
    @Override
    public String getFirstName() {
        return firstName;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setFirstName(String)
     */
    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getMiddleName()
     */
    @Override
    public String getMiddleName() {
        return middleName;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setMiddleName(String)
     */
    @Override
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getLastName()
     */
    @Override
    public String getLastName() {
        return lastName;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setLastName(String)
     */
    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getFullName()
     */
    @Override
    public String getFullName() {
        return String.format("%s %s%s", lastName == null ? "" : lastName, middleName == null ? "" : middleName,
                firstName == null ? "" : firstName);
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getDesc()
     */
    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setDesc(String)
     */
    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getSex()
     */
    @Override
    public Sex getSex() {
        return sex;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setSex(Sex)
     */
    @Override
    public void setSex(Sex sex) {
        this.sex = sex;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getBirthday()
     */
    @Override
    public Date getBirthday() {
        return birthday;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setBirthday(Date)
     */
    @Override
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getDepartment()
     */
    @Override
    public Department getDepartment() {
        return department;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setDepartment(Department)
     */
    @Override
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getSubordinates()
     */
    @Override
    public List<User> getSubordinates() {
        return subordinates;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setSubordinates(List)
     */
    @Override
    public void setSubordinates(List<User> subordinates) {
        this.subordinates = subordinates;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#getStation()
     */
    @Override
    public String getStation() {
        return station;
    }

    /**
     * {@inheritDoc}
     *
     * @see User#setStation(String)
     */
    @Override
    public void setStation(String station) {
        this.station = station;
    }
}
