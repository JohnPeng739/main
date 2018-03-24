package org.mx.kbm.entity;

import org.mx.dal.entity.BaseDictTreeEntity;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 描述： 知识分类目录实体类，基于Mongodb实现。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午7:46
 */
@Document(collection = "category")
public class CategoryEntity extends BaseDictTreeEntity implements Category {
    @DBRef
    private Tenant tenant;

    /**
     * {@inheritDoc}
     * @see Category#getTenant()
     */
    @Override
    public Tenant getTenant() {
        return tenant;
    }

    /**
     * {@inheritDoc}
     * @see Category#setTenant(Tenant)
     */
    @Override
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
