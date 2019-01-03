package org.mx.dal.test.entity;

import org.mx.dal.annotation.ElasticField;
import org.mx.dal.annotation.ElasticIndex;
import org.mx.dal.entity.ElasticBaseEntity;

@ElasticIndex("school")
public class SchoolElasticEntity extends ElasticBaseEntity implements School {
    @ElasticField
    private String name;
    @ElasticField(type = "geo_point")
    private double[] location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}
