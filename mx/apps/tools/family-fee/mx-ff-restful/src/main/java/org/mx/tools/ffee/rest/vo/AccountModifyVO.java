package org.mx.tools.ffee.rest.vo;

import org.mx.dal.EntityFactory;
import org.mx.tools.ffee.dal.entity.FfeeAccount;

public class AccountModifyVO {
    private String id, nickname, mobile, email, wx, qq, wb, avatarUrl, country, province, city;
    private FfeeAccount.Gender gender = FfeeAccount.Gender.MALE;

    public FfeeAccount get() {
        FfeeAccount account = EntityFactory.createEntity(FfeeAccount.class);
        account.setId(id);
        account.setNickname(nickname);
        account.setMobile(mobile);
        account.setEmail(email);
        account.setWx(wx);
        account.setQq(qq);
        account.setWb(wb);
        account.setAvatarUrl(avatarUrl);
        account.setCountry(country);
        account.setProvince(province);
        account.setCity(city);
        return account;
    }

    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
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
}
