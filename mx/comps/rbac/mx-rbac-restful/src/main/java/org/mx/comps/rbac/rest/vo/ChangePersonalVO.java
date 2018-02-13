package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.service.AccountManageService;

import java.util.Set;

/**
 * 修改用户个性化信息的值对象定义
 *
 * @author : john.peng created on date : 2018/2/13
 */
public class ChangePersonalVO {
    private String id;
    private Set<String> favoriteTools;

    public AccountManageService.AccountPersonalInfo getAccountPersonalInfo() {
        return AccountManageService.AccountPersonalInfo.valueOf(id, favoriteTools);
    }

    public String getId() {
        return id;
    }

    public Set<String> getFavoriteTools() {
        return favoriteTools;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFavoriteTools(Set<String> favoriteTools) {
        this.favoriteTools = favoriteTools;
    }
}
