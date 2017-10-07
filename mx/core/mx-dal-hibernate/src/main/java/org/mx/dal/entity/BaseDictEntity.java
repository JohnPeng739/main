package org.mx.dal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by john on 2017/10/6.
 */
@Entity
public class BaseDictEntity extends BaseEntity implements BaseDict {
    @Column(unique = true)
    private String code;
    private String name, desc;

    @Override
    public String toString() {
        return super.toString() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'';
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
