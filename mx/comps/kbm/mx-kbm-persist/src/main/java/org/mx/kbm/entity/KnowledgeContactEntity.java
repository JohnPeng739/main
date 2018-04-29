package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 描述： 基于Hibernate JPA实现的联系人实体类
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午11:02
 */
@Entity
@Table(name = "TB_CONTACT")
public class KnowledgeContactEntity extends BaseEntity implements KnowledgeContact {
    @OneToOne
    private Account account;
    @Column(name = "MOBILE")
    private String mobile;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "ADDRESS")
    private String address;

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }
}
