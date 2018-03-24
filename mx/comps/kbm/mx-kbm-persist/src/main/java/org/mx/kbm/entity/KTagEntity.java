package org.mx.kbm.entity;

import org.mx.dal.entity.BaseEntity;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 描述： 知识标签实体类，基于Mongodb实现。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午8:24
 */
@Document(collection = "ktag")
public class KTagEntity extends BaseEntity implements KTag {
    @TextIndexed
    private String tag = null, desc = null;
    @DBRef
    private Tenant tenant = null;

    /**
     * {@inheritDoc}
     *
     * @see KTag#getTag()
     */
    @Override
    public String getTag() {
        return tag;
    }

    /**
     * {@inheritDoc}
     *
     * @see KTag#setTag(String)
     */
    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * {@inheritDoc}
     *
     * @see KTag#getDesc()
     */
    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see KTag#setDesc(String)
     */
    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see KTag#getTenant()
     */
    @Override
    public Tenant getTenant() {
        return tenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KTag#setTenant(Tenant)
     */
    @Override
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
