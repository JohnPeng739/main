package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.dal.EntityFactory;
import org.mx.service.rest.vo.BaseDictTreeVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 部门值对象定义
 *
 * @author : john.peng created on date : 2017/11/8
 */
public class DepartmentVO extends BaseDictTreeVO {
    private static final Log logger = LogFactory.getLog(DepartmentVO.class);

    private UserVO manager;
    private List<UserVO> employees;

    public static void transform(Department department, DepartmentVO departmentVO) {
        if (department == null || departmentVO == null) {
            return;
        }
        BaseDictTreeVO.transform(department, departmentVO);
        if (department.getManager() != null) {
            departmentVO.manager = new UserVO();
            UserVO.transform(department.getManager(), departmentVO.manager);
        }
        Set<User> employees = department.getEmployees();
        if (employees != null && !employees.isEmpty()) {
            departmentVO.employees = UserVO.transformUserVOs(employees);
        }
    }

    public static void transform(DepartmentVO departmentVO, Department department) {
        if (department == null || departmentVO == null) {
            return;
        }
        BaseDictTreeVO.transform(departmentVO, department);
        if (departmentVO.getManager() != null) {
            User manager = EntityFactory.createEntity(User.class);
            UserVO.transform(departmentVO.getManager(), manager);
            department.setManager(manager);
        }
        List<UserVO> employeeVOs = departmentVO.getEmployees();
        if (employeeVOs != null || employeeVOs.isEmpty()) {
            department.setEmployees(UserVO.transformUsers(employeeVOs));
        }
    }

    public static List<DepartmentVO> transformDepartmentVOs(Collection<Department> departments) {
        List<DepartmentVO> list = new ArrayList<>();
        if (departments != null && !departments.isEmpty()) {
            for (Department department : departments) {
                DepartmentVO vo = new DepartmentVO();
                DepartmentVO.transform(department, vo);
                list.add(vo);
            }
        }
        return list;
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
