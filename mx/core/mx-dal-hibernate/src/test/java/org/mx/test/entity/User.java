package org.mx.test.entity;

import org.mx.dal.entity.BaseDict;

/**
 * Created by john on 2017/10/6.
 */
public interface User extends BaseDict {
    void setEmail(String email);

    void setAddress(String address);

    void setPostCode(String postCode);

    void setMobile(String mobile);

    String getEmail();

    String getAddress();

    String getPostCode();

    String getMobile();
}
