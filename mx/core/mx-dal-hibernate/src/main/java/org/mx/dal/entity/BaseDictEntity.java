package org.mx.dal.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 基于Hibernate实现的基础字典实体
 *
 * @author : john.peng date : 2017/10/6
 * @see BaseEntity
 * @see BaseDict
 */
@MappedSuperclass
public class BaseDictEntity extends BaseEntity implements BaseDict {
    @Column(name = "CODE", nullable = false, unique = true, length = 30)
    private String code;
    @Column(name = "NAME", length = 100)
    private String name;
    @Column(name = "DESCRIPTION")
    private String desc;

    /**
     * {@inheritDoc}
     * @see BaseEntity#toString()
     */
    @Override
    public String toString() {
        return super.toString() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'';
    }

    /**
     * {@inheritDoc}
     * @see BaseDict#getCode()
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     * @see BaseDict#setCode(String)
     */
    @Override
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     * @see BaseDict#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * @see BaseDict#setName(String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     * @see BaseDict#getDesc()
     */
    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     * @see BaseDict#setDesc(String)
     */
    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
