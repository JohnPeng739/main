package org.mx.dal.test.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "T_SCHOOL")
public class SchoolJpaEntity extends BaseEntity implements School {
    @Column(name = "NAME")
    private String name;
    @Column(name = "LOCATION")
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
