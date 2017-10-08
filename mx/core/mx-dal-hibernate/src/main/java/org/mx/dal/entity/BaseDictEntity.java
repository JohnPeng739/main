package org.mx.dal.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by john on 2017/10/6.
 */
@MappedSuperclass
public class BaseDictEntity extends BaseEntity implements BaseDict {
    @Column(name = "CODE", nullable = false, unique = true, length = 30)
    private String code;
    @Column(name = "NAME", length = 100)
    private String name;
    @Column(name = "DESC")
    private String desc;

    @Override
    public String toString() {
        return super.toString() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'';
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
