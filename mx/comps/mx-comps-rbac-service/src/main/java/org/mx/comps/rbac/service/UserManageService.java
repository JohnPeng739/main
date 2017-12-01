package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.dal.service.GeneralAccessor;

import java.util.List;

/**
 * 用户管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/10
 */
public interface UserManageService extends GeneralAccessor {
    /**
     * 为指定的用户分配一个账户
     *
     * @param accountInfo 账户信息
     * @return 分配成功的账户实体对象
     */
    Account allocateAccount(AccountInfo accountInfo);

    /**
     * 修改用户信息
     *
     * @param user 用户实体
     * @return 修改后的用户实体
     */
    User saveUser(User user);

    /**
     * 分配一个新账户的必备信息定义
     *
     * @author : john.peng created on date : 2017/11/29
     */
    class AccountInfo {
        private String code, password, userId;
        private List<String> roleIds;

        private AccountInfo() {
            super();
        }

        private AccountInfo(String userId, String code, String password, List<String> roleIds) {
            this();
            this.userId = userId;
            this.code = code;
            this.password = password;
            this.roleIds = roleIds;
        }

        public static AccountInfo valueOf(String userId, String code, String password, List<String> roleIds) {
            return new AccountInfo(userId, code, password, roleIds);
        }

        public String getCode() {
            return code;
        }

        public String getPassword() {
            return password;
        }

        public String getUserId() {
            return userId;
        }

        public List<String> getRoleIds() {
            return roleIds;
        }
    }
}
