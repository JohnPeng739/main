package org.mx.test.entity;

import org.mx.dal.entity.BaseDictTree;

/**
 * Created by john on 2017/10/6.
 */
public interface User extends BaseDictTree {
    String getEmail();

    void setEmail(String email);

    String getAddress();

    void setAddress(String address);

    String getPostCode();

    void setPostCode(String postCode);

    String getMobile();

    void setMobile(String mobile);
}
