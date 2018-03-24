package org.mx.kbm.entity;

import org.mx.dal.entity.BaseEntity;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述： 知识条目实体类，基于Mongodb实现
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午8:06
 */
@Document(collection = "knode")
public class KNodeEntity extends BaseEntity implements KNode {
    private int acl = KAcl.READ.getOrdinal();
    @TextIndexed
    private String summary = null;
    @DBRef
    private Tenant tenant = null;
    @DBRef
    private Set<KTag> tags;

    /**
     * 默认的构造函数
     */
    public KNodeEntity() {
        super();
        this.tags = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#getAcl()
     */
    @Override
    public int getAcl() {
        return acl;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#setAcl(int)
     */
    @Override
    public void setAcl(int acl) {
        this.acl = acl;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#getSummary()
     */
    @Override
    public String getSummary() {
        return summary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#setSummary(String)
     */
    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#getTenant()
     */
    @Override
    public Tenant getTenant() {
        return tenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#setTenant(Tenant)
     */
    @Override
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#getTags()
     */
    @Override
    public Set<KTag> getTags() {
        return tags;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#setTags(Set)
     */
    @Override
    public void setTags(Set<KTag> tags) {
        this.tags = tags;
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#setAclReadWrite()
     */
    @Override
    public void setAclReadWrite() {
        this.acl = KAcl.READ.getOrdinal() + KAcl.WRITE.getOrdinal();
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#setAclReadWriteDelete()
     */
    @Override
    public void setAclReadWriteDelete() {
        this.acl = KAcl.READ.getOrdinal() + KAcl.WRITE.getOrdinal() + KAcl.DELETE.getOrdinal();
    }

    /**
     * {@inheritDoc}
     *
     * @see KNode#setAclReadWriteShare()
     */
    @Override
    public void setAclReadWriteShare() {
        this.acl = KAcl.READ.getOrdinal() + KAcl.WRITE.getOrdinal() + KAcl.SHARE.getOrdinal();
    }
}
