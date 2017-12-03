package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.service.AccreditManageService;

import java.util.List;

/**
 * 操作授权信息的值对象定义
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class AccreditInfoVO {
    private String srcAccountId, tarAccountId, desc;
    private List<String> roleIds;
    private long startTime, endTime = -1;

    public AccreditManageService.AccreditInfo getAccreditInfo() {
        return AccreditManageService.AccreditInfo.valueOf(srcAccountId, tarAccountId, roleIds, startTime, endTime, desc);
    }

    public String getSrcAccountId() {
        return srcAccountId;
    }

    public void setSrcAccountId(String srcAccountId) {
        this.srcAccountId = srcAccountId;
    }

    public String getTarAccountId() {
        return tarAccountId;
    }

    public void setTarAccountId(String tarAccountId) {
        this.tarAccountId = tarAccountId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
