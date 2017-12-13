package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.User;

/**
 * 用户管理相关接口定义
 *
 * @author : john.peng created on date : 2017/11/10
 */
public interface UserManageService {
    /**
     * 为指定的用户分配一个账户
     *
     * @param accountInfo 账户信息
     * @return 分配成功的账户实体对象
     */
    Account allocateAccount(AccountManageService.AccountInfo accountInfo);

    /**
     * 保存用户信息
     *
     * @param userInfo 用户信息对象
     * @return 保存后的用户实体
     */
    User saveUser(UserInfo userInfo);

    class UserInfo {
        private String userId, firstName, middleName, lastName, station, desc, departId;
        private boolean valid = true;
        private User.Sex sex = User.Sex.FEMALE;
        private long birthday = -1;

        private UserInfo() {
            super();
        }

        private UserInfo(String firstName, String middleName, String lastName, User.Sex sex) {
            this();
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.sex = sex;
        }

        private UserInfo(String firstName, String middleName, String lastName, User.Sex sex, String userId,
                         long birthday, String departId, String station, boolean valid, String desc) {
            this(firstName, middleName, lastName, sex);
            this.userId = userId;
            if (birthday > 0) {
                this.birthday = birthday;
            }
            this.departId = departId;
            this.station = station;
            this.valid = valid;
            this.desc = desc;
        }

        public static final UserInfo valueOf(String firstName, String middleName, String lastName, User.Sex sex) {
            return new UserInfo(firstName, middleName, lastName, sex);
        }

        public static final UserInfo valueOf(String firstName, String middleName, String lastName, User.Sex sex,
                                             String userId, long birthday, String departId, String station,
                                             boolean valid, String desc) {
            return new UserInfo(firstName, middleName, lastName, sex, userId, birthday, departId, station, valid, desc);
        }

        public String getUserId() {
            return userId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getStation() {
            return station;
        }

        public String getDesc() {
            return desc;
        }

        public String getDepartId() {
            return departId;
        }

        public boolean isValid() {
            return valid;
        }

        public User.Sex getSex() {
            return sex;
        }

        public long getBirthday() {
            return birthday;
        }
    }
}
