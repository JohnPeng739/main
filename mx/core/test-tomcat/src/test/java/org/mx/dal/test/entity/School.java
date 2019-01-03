package org.mx.dal.test.entity;

import org.mx.dal.entity.Base;

public interface School extends Base {
    String getName();
    void setName(String name);
    double[] getLocation();
    void setLocation(double[] location);
}
