package com.ds.retl.rest.vo.topology;

import com.ds.retl.dal.entity.Topology;
import org.mx.rest.vo.BaseVO;

/**
 * Created by john on 2017/10/10.
 */
public class TopologyVO extends BaseVO {
    private boolean submitted;
    private long submittedTime;
    private String name, topologyContent, submitInfo, operator;

    public static void transform(Topology topology, TopologyVO topologyVO) {
        if (topology == null || topologyVO == null) {
            return;
        }
        BaseVO.transform(topology, topologyVO);
        topologyVO.name = topology.getName();
        topologyVO.submitted = topology.isSubmitted();
        topologyVO.submittedTime = topology.getSubmittedTime();
        topologyVO.topologyContent = topology.getTopologyContent();
        topologyVO.submitInfo = topology.getSubmitInfo();
        topologyVO.operator = topology.getOperator();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTopologyContent(String topologyContent) {
        this.topologyContent = topologyContent;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public void setSubmittedTime(long submittedTime) {
        this.submittedTime = submittedTime;
    }

    public void setSubmitInfo(String submitInfo) {
        this.submitInfo = submitInfo;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public String getTopologyContent() {
        return topologyContent;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public long getSubmittedTime() {
        return submittedTime;
    }

    public String getSubmitInfo() {
        return submitInfo;
    }

    @Override
    public String getOperator() {
        return operator;
    }
}
