package org.mx.dal;

import org.mx.dal.entity.Base;

public interface User extends Base {
    String getCode();
    void setCode(String code);
}
