package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.rest.vo.BaseDictTreeVO;

import java.util.ArrayList;
import java.util.List;

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
        List<User> employees = department.getEmployees();
        if (employees != null && !employees.isEmpty()) {
            List<UserVO> employeesVO = new ArrayList<>();
            employees.forEach(employee -> {
                if (employee != null) {
                    UserVO vo = new UserVO();
                    UserVO.transform(employee, vo);
                    employeesVO.add(vo);
                }
            });
            departmentVO.employees = employeesVO;
        }
    }

    public static void transform(DepartmentVO departmentVO, Department department) {
        if (department == null || departmentVO == null) {
            return;
        }
        BaseDictTreeVO.transform(departmentVO, department);
        if (departmentVO.getManager() != null) {
            try {
                User manager = EntityFactory.createEntity(User.class);
                UserVO.transform(departmentVO.getManager(), manager);
                department.setManager(manager);
            } catch (EntityInstantiationException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(ex);
                }
            }
        }
        List<UserVO> employeeVOs = departmentVO.getEmployees();
        if (employeeVOs != null || employeeVOs.isEmpty()) {
            department.setEmployees(UserVO.transform(employeeVOs));
        }
    }

    public void setManager(UserVO manager) {
        this.manager = manager;
    }

    public void setEmployees(List<UserVO> employees) {
        this.employees = employees;
    }

    public UserVO getManager() {
        return manager;
    }

    public List<UserVO> getEmployees() {
        return employees;
    }
}
