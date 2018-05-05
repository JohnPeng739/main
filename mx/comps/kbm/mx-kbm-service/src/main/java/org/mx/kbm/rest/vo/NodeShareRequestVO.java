package org.mx.kbm.rest.vo;

import org.mx.kbm.entity.KnowledgeAcl;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 知识条目分享请求值对象类定义，支持同时将多个知识条目分享个多个人。
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午11:22
 */
public class NodeShareRequestVO {
    private List<String> nodeIds;
    private List<String> beneficiaryIds = new ArrayList<>();
    private int acls = KnowledgeAcl.READ.getOrdinal();
    private long startTime = -1, endTime = -1;

    public List<String> getNodeIds() {
        return nodeIds;
    }

    public List<String> getBeneficiaryIds() {
        return beneficiaryIds;
    }

    public int getAcls() {
        return acls;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setNodeIds(List<String> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public void setBeneficiaryIds(List<String> beneficiaryIds) {
        this.beneficiaryIds = beneficiaryIds;
    }

    public void setAcls(int acls) {
        this.acls = acls;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
