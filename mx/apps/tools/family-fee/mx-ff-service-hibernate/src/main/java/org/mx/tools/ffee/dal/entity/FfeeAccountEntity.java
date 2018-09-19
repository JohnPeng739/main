package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 描述： FFEE应用中的账户实体类，基于Hibernate实现，依赖RBAC中的Account。
 *
 * @author John.Peng
 * Date time 2018/2/18 上午10:29
 */
@Entity
@Table(name = "TB_FFEE_ACCOUNT")
public class FfeeAccountEntity extends BaseEntity implements FfeeAccount {
    @Column(name = "OPEN_ID", length = 50)
    private String openId;
    @Column(name = "UNION_ID", length = 50)
    private String unionId;
    @Column(name = "NICKNAME", length = 20)
    private String nickname;
    @Column(name = "MOBILE", length = 20)
    private String mobile;
    @Column(name = "EMAIL", length = 30)
    private String email;
    @Column(name = "WX", length = 20)
    private String wx;
    @Column(name = "QQ", length = 20)
    private String qq;
    @Column(name = "WB", length = 20)
    private String wb;
    @Column(name = "AVATAR_URL", length = 200)
    private String avatarUrl;
    @Column(name = "COUNTRY", length = 20)
    private String country;
    @Column(name = "PROVINCE", length = 20)
    private String province;
    @Column(name = "CITY", length = 20)
    private String city;
    @Column(name = "GENDER", length = 10)
    private Gender gender = Gender.MALE;

    @Override
    public String getOpenId() {
        return openId;
    }

    @Override
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String getUnionId() {
        return unionId;
    }

    @Override
    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
    public String getWx() {
        return wx;
    }

    @Override
    public void setWx(String wx) {
        this.wx = wx;
    }

    @Override
    public String getQq() {
        return qq;
    }

    @Override
    public void setQq(String qq) {
        this.qq = qq;
    }

    @Override
    public String getWb() {
        return wb;
    }

    @Override
    public void setWb(String wb) {
        this.wb = wb;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String getProvince() {
        return province;
    }

    @Override
    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String getCity() {
        return city;
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
