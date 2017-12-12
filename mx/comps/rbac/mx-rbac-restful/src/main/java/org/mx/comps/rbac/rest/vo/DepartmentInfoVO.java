package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.service.DepartmentManageService;

import java.util.List;

/**
 * 操作部门信息的值对象
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class DepartmentInfoVO {
    private String code, name, desc, departId, managerId;
    private boolean valid = true;
    private List<String> employeeIds;

    public DepartmentManageService.DepartInfo getDepartInfo() {
        return DepartmentManageService.DepartInfo.valueOf(code, name, desc, departId, managerId, employeeIds, valid);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<String> employeeIds) {
        this.employeeIds = employeeIds;
    }
}
