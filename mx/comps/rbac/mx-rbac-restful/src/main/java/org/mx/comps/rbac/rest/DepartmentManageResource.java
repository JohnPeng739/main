package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.jwt.AuthenticateAround;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.rest.vo.DepartmentInfoVO;
import org.mx.comps.rbac.rest.vo.DepartmentVO;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.dal.Pagination;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    @Autowired
    private DepartmentManageService departmentManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("departments")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<List<DepartmentVO>> departments() {
        try {
            List<Department> departments = accessor.list(Department.class);
            List<DepartmentVO> vos = DepartmentVO.transformDepartmentVOs(departments);
            return new DataVO<>(vos);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List departments fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("departments")
    @POST
    @AuthenticateAround(returnValueClass = PaginationDataVO.class)
    public PaginationDataVO<List<DepartmentVO>> departments(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Department> departments = accessor.list(pagination, Department.class);
            List<DepartmentVO> vos = DepartmentVO.transformDepartmentVOs(departments);
            return new PaginationDataVO<>(pagination, vos);
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List departments fail.", ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("departments/{id}")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<DepartmentVO> getDepartment(@PathParam("id") String id) {
        try {
            Department department = accessor.getById(id, Department.class);
            DepartmentVO vo = new DepartmentVO();
            DepartmentVO.transform(department, vo);
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Get department fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    private DataVO<DepartmentVO> saveDepartment(DepartmentInfoVO departmentInfoVO) {
        try {

            Department department = departmentManageService.saveDepartment(departmentInfoVO.getDepartInfo());
            DepartmentVO vo = new DepartmentVO();
            DepartmentVO.transform(department, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Save department fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("departments/new")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<DepartmentVO> saveDepartment(@QueryParam("userCode") String userCode, DepartmentInfoVO departmentInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        departmentInfoVO.setDepartId(null);
        return saveDepartment(departmentInfoVO);
    }

    @Path("departments/{id}")
    @PUT
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<DepartmentVO> saveDepartment(@QueryParam("userCode") String userCode, @PathParam("id") String id,
                                               DepartmentInfoVO departmentInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        departmentInfoVO.setDepartId(id);
        return saveDepartment(departmentInfoVO);
    }

    @Path("departments/{id}")
    @DELETE
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<DepartmentVO> deleteDepartment(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Department department = accessor.remove(id, Department.class);
            DepartmentVO vo = new DepartmentVO();
            DepartmentVO.transform(department, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Delete department fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }
}
