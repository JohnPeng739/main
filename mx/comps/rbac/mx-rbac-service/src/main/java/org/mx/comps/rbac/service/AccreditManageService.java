package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Accredit;

import java.util.ArrayList;
import java.util.List;

/**
 * 授权管理服务接口定义
 *
 * @author : john.peng created on date : 2017/12/01
 */
public interface AccreditManageService {
    /**
     * 创建一个新的授权
     *
     * @param accreditInfo 授权信息对象
     * @return 成功授权后的授权信息实体对象
     */
    Accredit accredit(AccreditInfo accreditInfo);

    /**
     * 关闭指定的授权
     *
     * @param accreditId 授权对象ID
     * @return 成功关闭后的授权信息实体对象
     */
    Accredit closeAccredit(String accreditId);

    /**
     * 授权信息对象
     */
    class AccreditInfo {
        private String srcAccountId, tarAccountId, desc;
        private List<String> roleIds;
        private long startTime, endTime = -1;

        /**
         * 默认的构造函数
         */
        public AccreditInfo() {
            super();
            roleIds = new ArrayList<>();
        }

        /**
         * 默认的构造函数
         *
         * @param srcAccountId 授权源账户ID
         * @param tarAccountId 授权目标账户ID
         * @param roleIds      授权角色
         * @param startTime    授权生效时间
         * @param endTime      授权失效时间
         * @param desc         授权描述
         */
        public AccreditInfo(String srcAccountId, String tarAccountId, List<String> roleIds, long startTime,
                            long endTime, String desc) {
            this();
            this.srcAccountId = srcAccountId;
            this.tarAccountId = tarAccountId;
            if (roleIds != null) {
                this.roleIds.addAll(roleIds);
            }
            if (startTime > 0) {
                this.startTime = startTime;
            }
            if (endTime > 0 && endTime > startTime) {
                this.endTime = endTime;
            }
            this.desc = desc;
        }

        /**
         * 创建一个新的授权信息对象
         *
         * @param srcAccountId 授权源账户ID
         * @param tarAccountId 授权目标账户ID
         * @param roleIds      授权角色
         * @param startTime    授权生效时间
         * @param endTime      授权失效时间
         * @param desc         授权描述
         * @return 授权信息对象
         */
        public static AccreditInfo valueOf(String srcAccountId, String tarAccountId, List<String> roleIds,
                                           long startTime, long endTime, String desc) {
            return new AccreditInfo(srcAccountId, tarAccountId, roleIds, startTime, endTime, desc);
        }

        /**
         * 获取授权源账户ID
         *
         * @return 账户ID
         */
        public String getSrcAccountId() {
            return srcAccountId;
        }

        /**
         * 获取授权目标账户ID
         *
         * @return 账户ID
         */
        public String getTarAccountId() {
            return tarAccountId;
        }

        /**
         * 获取授权描述
         *
         * @return 描述
         */
        public String getDesc() {
            return desc;
        }

        /**
         * 获取授权角色ID列表
         *
         * @return 角色ID列表
         */
        public List<String> getRoleIds() {
            return roleIds;
        }

        /**
         * 获取授权生效时间
         *
         * @return 时间
         */
        public long getStartTime() {
            return startTime;
        }

        /**
         * 获取授权失效时间
         *
         * @return 时间
         */
        public long getEndTime() {
            return endTime;
        }
    }
}
