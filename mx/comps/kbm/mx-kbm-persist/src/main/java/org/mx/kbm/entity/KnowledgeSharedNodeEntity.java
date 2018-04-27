package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 描述： 基于Hibernate JPA实现的分享知识条目实体类
 *
 * @author John.Peng
 *         Date time 2018/4/27 下午4:19
 */
@Entity
@Table(name = "TB_KNODE_SHARED")
public class KnowledgeSharedNodeEntity extends BaseEntity implements KnowledgeSharedNode {
    private KnowledgeNode node;
    private int acls = KnowledgeAcl.READ.getOrdinal();
    private Account beneficiary;
    private long startTime;
    private long endTime;
    private SharedState state = SharedState.SHARED;

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#getNode()
     */
    @Override
    public KnowledgeNode getNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setNode(KnowledgeNode)
     */
    @Override
    public void setNode(KnowledgeNode node) {
        this.node = node;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#getAcls()
     */
    @Override
    public int getAcls() {
        return acls;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setAcls(int)
     */
    @Override
    public void setAcls(int acls) {
        this.acls = acls;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setAclsReadWrite()
     */
    @Override
    public void setAclsReadWrite() {
        this.acls = KnowledgeAcl.READ.getOrdinal() + KnowledgeAcl.WRITE.getOrdinal();
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setAclsReadWriteDelete()
     */
    @Override
    public void setAclsReadWriteDelete() {
        this.acls = KnowledgeAcl.READ.getOrdinal() + KnowledgeAcl.WRITE.getOrdinal() + KnowledgeAcl.DELETE.getOrdinal();
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setAclsReadWriteShare()
     */
    @Override
    public void setAclsReadWriteShare() {
        this.acls = KnowledgeAcl.READ.getOrdinal() + KnowledgeAcl.WRITE.getOrdinal() + KnowledgeAcl.SHARE.getOrdinal();
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#getBeneficiary()
     */
    @Override
    public Account getBeneficiary() {
        return beneficiary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setBeneficiary(Account)
     */
    @Override
    public void setBeneficiary(Account beneficiary) {
        this.beneficiary = beneficiary;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#getStartTime()
     */
    @Override
    public long getStartTime() {
        return startTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setStartTime(long)
     */
    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#getEndTime()
     */
    @Override
    public long getEndTime() {
        return endTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setEndTime(long)
     */
    @Override
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#getState()
     */
    @Override
    public SharedState getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     *
     * @see KnowledgeSharedNode#setState(SharedState)
     */
    @Override
    public void setState(SharedState state) {
        this.state = state;
    }
}
