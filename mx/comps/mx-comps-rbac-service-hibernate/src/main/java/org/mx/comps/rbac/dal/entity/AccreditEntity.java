package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 授权对象实体定义
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Entity
@Table(name = "TB_ACCREDIT")
public class AccreditEntity extends BaseEntity implements Accredit {
    @ManyToOne(targetEntity = AccountEntity.class)
    private Account src;
    @ManyToMany(targetEntity = RoleEntity.class, fetch = FetchType.EAGER)
    @JoinTable(name = "TB_ACCREDIT_ROLES",
            joinColumns = @JoinColumn(name = "ACCREDIT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
    private Set<Role> roles;
    @ManyToOne(targetEntity = AccountEntity.class)
    private Account tar;
    @Column(name = "START_TIME")
    private Date startTime;
    @Column(name = "END_TIME")
    private Date endTime;
    @Column(name = "DESCRIPTION")
    private String desc;

    public AccreditEntity() {
        super();
        roles = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     * @see Accredit#isClosed()
     */
    @Override
    public boolean isClosed() {
        if (!isValid()) {
            return true;
        }
        long endTime = -1;
        if (getEndTime() != null) {
            endTime = getEndTime().getTime();
        }
        if (endTime != -1l && endTime <= new Date().getTime()) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#getSrc()
     */
    @Override
    public Account getSrc() {
        return src;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#getRoles()
     */
    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#getTar()
     */
    @Override
    public Account getTar() {
        return tar;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#getStartTime()
     */
    @Override
    public Date getStartTime() {
        return startTime;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#getEndTime()
     */
    @Override
    public Date getEndTime() {
        return endTime;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#getDesc()
     */
    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#setSrc(Account)
     */
    @Override
    public void setSrc(Account src) {
        this.src = src;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#setRoles(Set)
     */
    @Override
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#setTar(Account)
     */
    @Override
    public void setTar(Account tar) {
        this.tar = tar;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#setStartTime(Date)
     */
    @Override
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#setEndTime(Date)
     */
    @Override
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * {@inheritDoc}
     * @see Accredit#setDesc(String)
     */
    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
