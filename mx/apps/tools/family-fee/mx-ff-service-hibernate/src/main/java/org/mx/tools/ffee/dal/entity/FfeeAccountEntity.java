package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 描述： FFEE应用中的账户实体类，基于Hibernate实现，依赖RBAC中的Account。
 *
 * @author John.Peng
 *         Date time 2018/2/18 上午10:29
 */
@Entity
@Table(name = "TB_FFEE_ACCOUNT")
public class FfeeAccountEntity extends BaseEntity implements FfeeAccount {
    @Column(name = "OPEN_ID")
    private String openId;
    @Column(name = "UNION_ID")
    private String unionId;
    @Column(name = "NICKNAME")
    private String nickname;
    @Column(name = "MOBILE")
    private String mobile;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "WX")
    private String wx;
    @Column(name = "QQ")
    private String qq;
    @Column(name = "WB")
    private String wb;
    @Column(name = "AVATAR_URL")
    private String avatarUrl;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "PROVINCE")
    private String province;
    @Column(name = "CITY")
    private String city;
    @Column(name = "GENDER")
    private Gender gender = Gender.MALE;

    @Override
    public String getOpenId() {
        return openId;
    }

    @Override
    public String getUnionId() {
        return unionId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getWx() {
        return wx;
    }

    @Override
    public String getQq() {
        return qq;
    }

    @Override
    public String getWb() {
        return wb;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getProvince() {
        return province;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setWx(String wx) {
        this.wx = wx;
    }

    @Override
    public void setQq(String qq) {
        this.qq = qq;
    }

    @Override
    public void setWb(String wb) {
        this.wb = wb;
    }

    @Override
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
