package org.mx.tools.ffee.service.bean;

import org.mx.tools.ffee.dal.entity.FfeeAccount;

public class AccountInfoBean {
    private String id, openId, unionId, nickname, mobile, email, wx, qq, wb, avatarUrl, country, province, city, password;
    private FfeeAccount.Gender gender;
    private boolean valid;

    public String getId() {
        return id;
    }

    public String getOpenId() {
        return openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getWx() {
        return wx;
    }

    public String getQq() {
        return qq;
    }

    public String getWb() {
        return wb;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public FfeeAccount.Gender getGender() {
        return gender;
    }

    public boolean isValid() {
        return valid;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public void setWb(String wb) {
        this.wb = wb;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGender(FfeeAccount.Gender gender) {
        this.gender = gender;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
