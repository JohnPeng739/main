package org.mx.dal.test.entity;

import org.mx.dal.annotation.ElasticIndex;
import org.mx.dal.entity.BaseDictEntity;

@ElasticIndex("user")
public class UserEntity extends BaseDictEntity {
    private int age = 0;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
