package org.mx.dal.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserEntityMongo extends MongoBaseDictTreeEntity<UserEntityMongo> implements User {
    private String email;
    @TextIndexed
    private String address;
    private String postCode;
    @Indexed
    private String mobile;

    @Override
    public String toString() {
        return "UserEntityMongo{" + super.toString() +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", postCode='" + postCode + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
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

    @Override
    public String getPostCode() {
        return postCode;
    }

    @Override
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
