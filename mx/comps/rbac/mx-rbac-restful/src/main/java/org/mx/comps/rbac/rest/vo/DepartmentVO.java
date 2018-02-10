package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.service.rest.vo.BaseDictVO;

import java.util.*;

/**
 * 部门值对象定义
 *
 * @author : john.peng created on date : 2017/11/8
 */
public class DepartmentVO extends BaseDictVO {
    private static final Log logger = LogFactory.getLog(DepartmentVO.class);

    private UserVO manager;
    private List<UserVO> employees;

    public static DepartmentVO transform(Department department, boolean iterate) {
        if (department == null) {
            return null;
        }
        DepartmentVO departmentVO = new DepartmentVO();
        BaseDictVO.transform(department, departmentVO);
        if (department.getManager() != null) {
            departmentVO.manager = UserVO.transform(department.getManager());
        }
        Set<User> employees = department.getEmployees();
        if (employees != null && !employees.isEmpty() && iterate) {
            departmentVO.employees = UserVO.transform(employees);
        }
        return departmentVO;
    }

    public static List<DepartmentVO> transform(Collection<Department> departments) {
        List<DepartmentVO> departmentVOS = new ArrayList<>();
        if (departments != null && !departments.isEmpty()) {
            for (Department department : departments) {
                DepartmentVO departmentVO = DepartmentVO.transform(department, false);
                departmentVOS.add(departmentVO);
            }
        }
        return departmentVOS;
    }

    public UserVO getManager() {
        return manager;
    }

    public void setManager(UserVO manager) {
        this.manager = manager;
    }

    public List<UserVO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<UserVO> employees) {
        this.employees = employees;
    }
}
