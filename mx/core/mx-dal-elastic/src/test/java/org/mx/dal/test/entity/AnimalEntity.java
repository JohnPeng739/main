package org.mx.dal.test.entity;

import org.mx.dal.annotation.ElasticField;
import org.mx.dal.annotation.ElasticIndex;
import org.mx.dal.entity.BaseEntity;

@ElasticIndex("animal")
public class AnimalEntity extends BaseEntity {
    @ElasticField(analyzer = "hanlp")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
