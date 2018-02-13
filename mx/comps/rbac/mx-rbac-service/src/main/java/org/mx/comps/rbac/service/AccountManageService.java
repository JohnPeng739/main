package org.mx.comps.rbac.service;

import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 账户管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/13
 */
public interface AccountManageService {
    /**
     * 修改账户信息，这里不允许修改账户的密码；如果需要修改密码，必须使用<code>changePassword</code>方法。
     * 注意：如果是新增账户，系统会根据传入的密码进行设置，如果传入的密码为空，则使用默认密码"ds110119"。
     *
     * @param accountInfo 账户信息对象
     * @return 修改成功后的账户实体
     */
    Account saveAccount(AccountInfo accountInfo);

    /**
     * 修改账户密码
     *
     * @param accountId   账户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 账户对象
     */
    Account changePassword(String accountId, String oldPassword, String newPassword);

    /**
     * 修改账户个性化信息
     *
     * @param accountPersonalInfo 账户个性化信息对象
     * @return 账户对象
     */
    Account changePersonal(AccountPersonalInfo accountPersonalInfo);

    /**
     * 登录系统
     *
     * @param accountCode 账户代码
     * @param password    账户密码
     * @param forced      如果设置为true，则如果发生该账户已经登录的话仍然强制登录（踢掉上次登录），否则返回异常。
     * @return 登录历史对象
     */
    LoginHistory login(String accountCode, String password, boolean forced);

    /**
     * 登出系统
     *
     * @param accountId 账户ID
     * @return 登录历史对象
     */
    LoginHistory logout(String accountId);

    class AccountInfo {
        private String code, password, desc, accountId, ownerId;
        private boolean valid = true;
        private List<String> roleIds;

        private AccountInfo() {
            super();
            this.roleIds = new ArrayList<>();
        }

        private AccountInfo(String code, String password, String desc) {
            this();
            this.code = code;
            this.password = password;
            this.desc = desc;
        }

        private AccountInfo(String code, String password, String desc, String accountId, String ownerId,
                            List<String> roleIds, boolean valid) {
            this(code, password, desc);
            this.accountId = accountId;
            this.ownerId = ownerId;
            this.roleIds = roleIds;
            this.valid = valid;
        }

        public static final AccountInfo valueOf(String code, String password, String desc) {
            return new AccountInfo(code, password, desc);
        }

        public static final AccountInfo valueOf(String code, String password, String desc, String accountId, String ownerId,
                                                List<String> roleIds, boolean valid) {
            return new AccountInfo(code, password, desc, accountId, ownerId, roleIds, valid);
        }

        public String getCode() {
            return code;
        }

        public String getPassword() {
            return password;
        }

        public String getDesc() {
            return desc;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getRoleIds() {
            return roleIds;
        }
    }

    class AccountPersonalInfo {
        private String id;
        private String favoriteTools;

        public static final AccountPersonalInfo valueOf(String id, Set<String> favoriteTools) {
            AccountPersonalInfo info = new AccountPersonalInfo();
            info.id = id;
            info.favoriteTools = StringUtils.merge(favoriteTools, ",");
            return info;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFavoriteTools() {
            return favoriteTools;
        }

        public void setFavoriteTools(String favoriteTools) {
            this.favoriteTools = favoriteTools;
        }
    }
}
