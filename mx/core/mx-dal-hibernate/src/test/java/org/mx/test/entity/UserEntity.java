package org.mx.test.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by john on 2017/10/6.
 */
@Entity(name = "User")
@Table(name = "TB_USER")
public class UserEntity extends BaseDictTreeEntity implements User {
    private String email, address, postCode,  mobile;

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
