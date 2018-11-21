package org.mx.dal.test.entity;

import org.mx.dal.annotation.ElasticField;
import org.mx.dal.annotation.ElasticIndex;
import org.mx.dal.entity.ElasticBaseDictEntity;

@ElasticIndex("user")
public class UserEntityElastic extends ElasticBaseDictEntity {
    @ElasticField(type = "long")
    private int age = 0;
    @ElasticField
    private String yt;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getYt() {
        return yt;
    }

    public void setYt(String yt) {
        this.yt = yt;
    }
}
