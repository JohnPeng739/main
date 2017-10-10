package com.ds.retl.dal.entity;

import org.mx.StringUtils;
import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by john on 2017/10/10.
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
    private String topologyContent;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setTopologyContent(String topologyContent) {
        this.topologyContent = topologyContent;
    }

    @Override
    public void setSubmittedTime(long submittedTime) {
        this.submittedTime = submittedTime;
    }

    @Override
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTopologyContent() {
        return topologyContent;
    }

    @Override
    public long getSubmittedTime() {
        return submittedTime;
    }

    @Override
    public boolean isSubmitted() {
        return submitted;
    }
}
