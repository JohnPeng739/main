package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.rest.vo.DepartmentVO;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmentManageResource {
    private static final Log logger = LogFactory.getLog(DepartmentManageResource.class);

    @Autowired
    private DepartmentManageService departmentManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("departments")
    @GET
    public DataVO<List<DepartmentVO>> departments() {
        try {
            List<Department> departments = departmentManageService.list(Department.class);
            List<DepartmentVO> vos = DepartmentVO.transformDepartmentVOs(departments);
            return new DataVO<>(vos);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("departments")
    @POST
    public PaginationDataVO<List<DepartmentVO>> departments(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Department> departments = departmentManageService.list(pagination, Department.class);
            List<DepartmentVO> vos = DepartmentVO.transformDepartmentVOs(departments);
            return new PaginationDataVO<>(pagination, vos);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(ex);
        }
    }

    @Path("departments/{id}")
    @GET
    public DataVO<DepartmentVO> getDepartment(@PathParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            return new DataVO<>(new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        try {
            Department department = departmentManageService.getById(id, Department.class);
            DepartmentVO vo = new DepartmentVO();
            DepartmentVO.transform(department, vo);
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("departments/new")
    @POST
    public DataVO<DepartmentVO> saveDepartment(@QueryParam("userCode") String userCode, DepartmentVO departmentVO) {
        if (StringUtils.isBlank(userCode)) {
            return new DataVO<>(new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Department department = EntityFactory.createEntity(Department.class);
            DepartmentVO.transform(departmentVO, department);
            department = departmentManageService.save(department);
            DepartmentVO vo = new DepartmentVO();
            DepartmentVO.transform(department, vo);
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("departments/{id}")
    @PUT
    public DataVO<DepartmentVO> saveDepartment(@QueryParam("userCode") String userCode, @PathParam("id") String id, DepartmentVO departmentVO) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(userCode)) {
            return new DataVO<>(new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Department department = EntityFactory.createEntity(Department.class);
            DepartmentVO.transform(departmentVO, department);
            department.setId(id);
            department = departmentManageService.save(department);
            DepartmentVO vo = new DepartmentVO();
            DepartmentVO.transform(department, vo);
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("departments/{id}")
    @DELETE
    public DataVO<DepartmentVO> deleteDepartment(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(userCode)) {
            return new DataVO<>(new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Department department = departmentManageService.remove(id, Department.class);
            DepartmentVO vo = new DepartmentVO();
            DepartmentVO.transform(department, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }
}
