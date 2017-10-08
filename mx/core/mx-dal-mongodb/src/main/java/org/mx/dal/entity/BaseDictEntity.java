package org.mx.dal.entity;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by john on 2017/10/8.
 */
public class BaseDictEntity extends BaseEntity implements BaseDict {
    @Indexed(unique = true)
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
