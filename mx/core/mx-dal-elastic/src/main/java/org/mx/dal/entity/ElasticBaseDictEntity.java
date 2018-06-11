package org.mx.dal.entity;

import org.mx.dal.annotation.ElasticField;

/**
 * 描述： 基于Elastic实现的基础字典实体
 *
 * @author John.Peng
 *         Date time 2018/4/1 上午8:45
 */
public class ElasticBaseDictEntity extends ElasticBaseEntity implements BaseDict {
    private String code;
    @ElasticField(analyzer = "hanlp")
    private  String name;
    @ElasticField(analyzer = "hanlp")
    private  String desc;

    /**
     * {@inheritDoc}
     *
     * @see BaseDict#getCode()
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDict#setCode(String)
     */
    @Override
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDict#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDict#getName()
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDict#getDesc()
     */
    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDict#setDesc(String)
     */
    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
