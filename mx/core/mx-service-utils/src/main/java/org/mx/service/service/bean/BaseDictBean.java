package org.mx.service.service.bean;

import org.mx.dal.entity.BaseDict;

/**
 * 描述： 基础字典信息对象类定义
 *
 * @author john peng
 * Date time 2018/5/6 下午3:05
 */
public class BaseDictBean extends BaseBean implements BaseDict {
    private String code, name, desc;

    /**
     * 数据对象属性复制
     *
     * @param src 源对象
     * @param tar 目标对象
     */
    public static void transform(BaseDict src, BaseDict tar) {
        BaseBean.transform(src, tar);
        tar.setCode(src.getCode());
        tar.setName(src.getName());
        tar.setDesc(src.getDesc());
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#toString()
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
     * @see BaseDict#setName(String)
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
