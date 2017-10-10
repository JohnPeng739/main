package com.ds.retl.dal.entity;

import org.mx.dal.entity.Base;

/**
 * Created by john on 2017/10/10.
 */
public interface Topology extends Base {
    String getName();
    void setName(String name);
    boolean isSubmitted();
    void setSubmitted(boolean submitted);
    long getSubmittedTime();
    void setSubmittedTime(long submittedTime);
    String getTopologyContent();
    void setTopologyContent(String topologyContent);
}
