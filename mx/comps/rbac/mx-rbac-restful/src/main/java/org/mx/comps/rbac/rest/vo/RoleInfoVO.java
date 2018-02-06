package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.service.RoleManageService;

import java.util.List;

/**
 * 操作角色信息的值对象定义
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class RoleInfoVO {
    private String is, code, name, desc;
    private boolean valid = true;
    private List<String> accountIds, privilegeIds;

    public RoleManageService.RoleInfo getRoleInfo() {
        return RoleManageService.RoleInfo.valueOf(code, name, desc, is, accountIds, privilegeIds, valid);
    }

    public void setId(String is) {
        this.is = is;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    public void setPrivilegeIds(List<String> privilegeIds) {
        this.privilegeIds = privilegeIds;
    }

    public String getId() {
        return is;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getAccountIds() {
        return accountIds;
    }

    public List<String> getPrivilegeIds() {
        return privilegeIds;
    }
}
