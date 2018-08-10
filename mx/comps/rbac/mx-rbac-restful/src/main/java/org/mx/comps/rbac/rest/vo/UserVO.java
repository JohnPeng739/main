package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.service.rest.vo.BaseVO;

import java.util.*;

/**
 * 用户值对象定义
 *
 * @author : john.peng created on date : 2017/11/8
 */
public class UserVO extends BaseVO {
    private static final Log logger = LogFactory.getLog(UserVO.class);

    private String firstName, lastName, fullName, desc, station;
    private User.Sex sex;
    private long birthday;
    private DepartmentVO department;

    public static UserVO transform(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BaseVO.transform(user, userVO);
        userVO.firstName = user.getFirstName();
        userVO.lastName = user.getLastName();
        userVO.fullName = user.getFullName();
        userVO.desc = user.getDesc();
        userVO.station = user.getStation();
        userVO.sex = user.getSex();
        userVO.birthday = user.getBirthday() != null ? user.getBirthday().getTime() : 0;
        if (user.getDepartment() != null) {
            userVO.department = DepartmentVO.transform(user.getDepartment(), false);
        }
        return userVO;
    }

    public static List<UserVO> transform(Collection<User> users) {
        List<UserVO> userVOS = new ArrayList<>();
        if (users == null) {
            return null;
        }
        users.forEach(user -> {
            UserVO userVO = UserVO.transform(user);
            if (userVO != null) {
                userVOS.add(userVO);
            }
        });
        return userVOS;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
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

    public DepartmentVO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentVO department) {
        this.department = department;
    }
}
