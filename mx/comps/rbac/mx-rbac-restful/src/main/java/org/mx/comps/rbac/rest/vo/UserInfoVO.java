package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.service.UserManageService;

/**
 * 操作用户信息的值对象定义
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class UserInfoVO {
    private String userId, firstName, middleName, lastName, station, desc, departId;
    private boolean valid = true;
    private User.Sex sex = User.Sex.FEMALE;
    private long birthday = -1;

    public UserManageService.UserInfo getUserInfo() {
        return UserManageService.UserInfo.valueOf(firstName, middleName, lastName, sex, userId, birthday,
                departId, station, valid, desc);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public User.Sex getSex() {
        return sex;
    }

    public void setSex(User.Sex sex) {
        this.sex = sex;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }
}
