package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.service.AccountManageService;

import java.util.List;

/**
 * 操作账户信息的值对象定义
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class AccountInfoVO {
    private String code, password, desc, id, ownerId;
    private boolean valid = true;
    private List<String> roleIds;

    public AccountManageService.AccountInfo getAccountInfo() {
        return AccountManageService.AccountInfo.valueOf(code, password, desc, id, ownerId, roleIds, valid);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }
}
