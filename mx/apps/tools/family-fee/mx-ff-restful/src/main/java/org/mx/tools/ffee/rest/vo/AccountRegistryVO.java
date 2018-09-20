package org.mx.tools.ffee.rest.vo;

import org.mx.dal.EntityFactory;
import org.mx.tools.ffee.dal.entity.FfeeAccount;

public class AccountRegistryVO extends BaseParamsVO {
    private String unionId, nickname, mobile, avatarUrl, country, province, city;

    public FfeeAccount get() {
        FfeeAccount account = EntityFactory.createEntity(FfeeAccount.class);
        account.setOpenId(super.getOpenId());
        account.setUnionId(unionId);
        account.setNickname(nickname);
        account.setMobile(mobile);
        account.setAvatarUrl(avatarUrl);
        account.setCountry(country);
        account.setProvince(province);
        account.setCity(city);
        return account;
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

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
}
