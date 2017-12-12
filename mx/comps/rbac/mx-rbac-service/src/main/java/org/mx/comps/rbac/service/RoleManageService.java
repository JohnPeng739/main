package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.service.GeneralDictAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/23
 */
public interface RoleManageService {
    /**
     * 保存角色相关信息
     *
     * @param roleInfo 角色信息对象
     * @return 保存后的角色实体对象
     */
    Role saveRole(RoleInfo roleInfo);

    class RoleInfo {
        private String roleId, code, name, desc;
        private boolean valid = true;
        private List<String> accountIds, privilegeIds;

        private RoleInfo() {
            super();
            accountIds = new ArrayList<>();
            privilegeIds = new ArrayList<>();
        }

        private RoleInfo(String code, String name, String desc) {
            this();
            this.code = code;
            this.name = name;
            this.desc = desc;
        }

        private RoleInfo(String code, String name, String desc, String roleId, List<String> accountIds,
                         List<String> privilegeIds, boolean valid) {
            this(code, name, desc);
            this.roleId = roleId;
            this.accountIds = accountIds;
            this.privilegeIds = privilegeIds;
            this.valid = valid;
        }

        public static final RoleInfo valueOf(String code, String name, String desc) {
            return new RoleInfo(code, name, desc);
        }

        public static final RoleInfo valueOf(String code, String name, String desc, String roleId, List<String> accountIds,
                                             List<String> privilegeIds, boolean valid) {
            return new RoleInfo(code, name, desc, roleId, accountIds, privilegeIds, valid);
        }

        public String getRoleId() {
            return roleId;
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
}
