package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户对象实体定义，使用MongoDB
 *
 * @author : john.peng created on date : 2017/12/12
 */
@Document(collection = "user")
public class UserEntity extends MongoBaseEntity implements User {
    private String firstName, middleName, lastName;
    @TextIndexed
    private String desc;
    @Indexed
    private Sex sex;
    private Date birthday;
    @DBRef
    private Department department;
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
        return String.format("%s%s%s", lastName == null ? "" : lastName + " ", middleName == null ? "" : middleName,
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
