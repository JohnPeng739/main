package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
    @Column(name = "DESCRIPTION", length = 100)
    private String description;
    @Column(name = "SUBMITTED_TIME")
    private long submittedTime;
    @Column(name = "SUBMITTED")
    private boolean submitted;
    @Column(name = "TOPOLOGY_ID")
    private String topologyId;
    @Column(name = "CONFIG_CONTENT", nullable = false)
    @Lob
    private String topologyContent = "";
    @Column(name = "SUBMIT_INFO")
    @Lob
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
     * @see Topology#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#setDescription(String)
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
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
     * @see Topology#getTopologyId()
     */
    @Override
    public String getTopologyId() {
        return topologyId;
    }

    /**
     * {@inheritDoc}
     *
     * @see Topology#setTopologyId(String)
     */
    @Override
    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
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
