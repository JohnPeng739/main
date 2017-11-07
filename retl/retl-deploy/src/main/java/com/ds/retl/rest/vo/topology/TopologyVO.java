package com.ds.retl.rest.vo.topology;

import com.ds.retl.dal.entity.Topology;
import org.mx.rest.vo.BaseVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/10/10.
 */
public class TopologyVO extends BaseVO {
    private boolean submitted;
    private long submittedTime;
    private String name, topologyContent, submitInfo, operator, topologyId;

    public static List<TopologyVO> transform(List<Topology> topologies) {
        List<TopologyVO> list = new ArrayList<>();
        if (topologies != null && !topologies.isEmpty()) {
            topologies.forEach(topology -> {
                TopologyVO vo = new TopologyVO();
                TopologyVO.transform(topology, vo);
                list.add(vo);
            });
        }
        return list;
    }

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
        topologyVO.topologyId = topology.getTopologyId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopologyContent() {
        return topologyContent;
    }

    public void setTopologyContent(String topologyContent) {
        this.topologyContent = topologyContent;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public long getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(long submittedTime) {
        this.submittedTime = submittedTime;
    }

    public String getSubmitInfo() {
        return submitInfo;
    }

    public void setSubmitInfo(String submitInfo) {
        this.submitInfo = submitInfo;
    }

    public String getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }
}
