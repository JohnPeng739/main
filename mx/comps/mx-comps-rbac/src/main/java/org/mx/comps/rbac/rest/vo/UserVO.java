package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.dal.entity.User;
import org.mx.rest.vo.BaseVO;

/**
 * 用户值对象定义
 *
 * @author : john.peng created on date : 2017/11/8
 */
public class UserVO extends BaseVO {
    private String firstName, middleName, lastName, fullName, desc, station;
    private User.Sex sex;
    private long birthday;
    private DepartmentVO department;
    private List<UserVO> subordinates;
}
