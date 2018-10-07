package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： FFEE中的账户信息接口定义，依赖RBAC中的Account。
 *
 * @author John.Peng
 * Date time 2018/2/17 下午4:28
 */
public interface FfeeAccount extends Base {
    String getOpenId();

    void setOpenId(String openId);

    String getUnionId();

    void setUnionId(String unionId);

    String getNickname();

    void setNickname(String nickname);

    Gender getGender();

    void setGender(Gender gender);

    String getMobile();

    void setMobile(String mobile);

    String getEmail();

    void setEmail(String email);

    String getWx();

    void setWx(String wx);

    String getQq();

    void setQq(String qq);

    String getWb();

    void setWb(String wb);

    String getAvatarUrl();

    void setAvatarUrl(String avatarUrl);

    String getCountry();

    void setCountry(String country);

    String getProvince();

    void setProvince(String province);

    String getCity();

    void setCity(String city);

    String getPassword();

    void setPassword(String password);

    enum Gender {
        MALE, FEMALE, NA
    }
}
