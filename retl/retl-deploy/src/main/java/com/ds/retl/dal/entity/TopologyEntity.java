package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 基于Hibernate JPA实现的计算拓扑实体类
 *
 * @author : john.peng created on date : 2017/10/10
 * @see Topology
 */
@Entity
@Table(name = "TB_TOPOLOGY")
public class TopologyEntity extends BaseEntity implements Topology {
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;
    @Column(name = "SUBMITTED_TIME")
    private long submittedTime;
    @Column(name = "SUBMITTED")
    private boolean submitted;
    @Column(nullable = false, columnDefinition = "clob")
    private String topologyContent = "";
    @Column(nullable = false, columnDefinition = "clob")
    private String submitInfo = "";

    /**
     * {@inheritDoc}
     *
     * @see Topology#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#setName(String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#getTopologyContent()
     */
    @Override
    public String getTopologyContent() {
        return topologyContent;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#setTopologyContent(String)
     */
    @Override
    public void setTopologyContent(String topologyContent) {
        this.topologyContent = topologyContent;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#getSubmittedTime()
     */
    @Override
    public long getSubmittedTime() {
        return submittedTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#setSubmittedTime(long)
     */
    @Override
    public void setSubmittedTime(long submittedTime) {
        this.submittedTime = submittedTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#isSubmitted()
     */
    @Override
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#setSubmitted
     */
    @Override
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#getSubmitInfo()
     */
    @Override
    public String getSubmitInfo() {
        return submitInfo;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#setSubmitInfo(String)
     */
    @Override
    public void setSubmitInfo(String submitInfo) {
        this.submitInfo = submitInfo;
    }
}
