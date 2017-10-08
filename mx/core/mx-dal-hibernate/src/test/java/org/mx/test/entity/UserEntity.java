package org.mx.test.entity;

import org.mx.StringUtils;
import org.mx.dal.entity.BaseDictTreeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by john on 2017/10/6.
 */
@Entity(name = "User")
@Table(name = "TB_USER")
public class UserEntity extends BaseDictTreeEntity implements User {
    @Column(name = "EMAIL", length = 50)
    private String email;
    @Column(name = "ADDRESS", length = 255)
    private String address;
    @Column(name = "POST_CODE", length = 20)
    private String postCode;
    @Column(name = "MOBILE", length = 20)
    private String mobile;

    @Override
    public String toString() {
        return "UserEntity{" + super.toString() +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", postCode='" + postCode + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void setPostCode(String postCode) {
        this.postCode = postCode;
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
    public String getAddress() {
        return address;
    }

    @Override
    public String getPostCode() {
        return postCode;
    }

    @Override
    public String getMobile() {
        return mobile;
    }
}
