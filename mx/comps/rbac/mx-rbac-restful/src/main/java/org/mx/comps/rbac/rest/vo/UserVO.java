package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.dal.EntityFactory;
import org.mx.service.rest.vo.BaseDictVO;
import org.mx.service.rest.vo.BaseVO;

import java.util.*;

/**
 * 用户值对象定义
 *
 * @author : john.peng created on date : 2017/11/8
 */
public class UserVO extends BaseVO {
    private static final Log logger = LogFactory.getLog(UserVO.class);

    private String firstName, middleName, lastName, fullName, desc, station;
    private User.Sex sex;
    private long birthday;
    private DepartmentVO department;

    public static void transform(User user, UserVO userVO) {
        if (user == null || userVO == null) {
            return;
        }
        BaseVO.transform(user, userVO);
        userVO.firstName = user.getFirstName();
        userVO.middleName = user.getMiddleName();
        userVO.lastName = user.getLastName();
        userVO.fullName = user.getFullName();
        userVO.desc = user.getDesc();
        userVO.station = user.getStation();
        userVO.sex = user.getSex();
        userVO.birthday = user.getBirthday() != null ? user.getBirthday().getTime() : 0;
        if (user.getDepartment() != null) {
            DepartmentVO departmentVO = new DepartmentVO();
            BaseDictVO.transform(user.getDepartment(), departmentVO);
            userVO.department = departmentVO;
        }
    }

    public static void transform(UserVO userVO, User user) {
        if (user == null || userVO == null) {
            return;
        }
        BaseVO.transform(userVO, user);
        user.setFirstName(userVO.getFirstName());
        user.setMiddleName(userVO.getMiddleName());
        user.setLastName(userVO.getLastName());
        user.setDesc(userVO.getDesc());
        user.setStation(userVO.getStation());
        user.setSex(userVO.getSex());
        user.setBirthday(new Date(userVO.getBirthday()));
        if (userVO.getDepartment() != null) {
            Department department = EntityFactory.createEntity(Department.class);
            BaseDictVO.transform(userVO.getDepartment(), department);
            user.setDepartment(department);
        }
    }

    public static Set<User> transformUsers(List<UserVO> userVOS) {
        if (userVOS == null) {
            return null;
        }
        Set<User> users = new HashSet<>();
        userVOS.forEach(subordinateVO -> {
            User user = EntityFactory.createEntity(User.class);
            UserVO.transform(subordinateVO, user);
            users.add(user);
        });
        return users;
    }

    public static List<UserVO> transformUserVOs(Collection<User> users) {
        if (users == null) {
            return null;
        }
        List<UserVO> userVOS = new ArrayList<>();
        users.forEach(user -> {
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            userVOS.add(userVO);
        });
        return userVOS;
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
