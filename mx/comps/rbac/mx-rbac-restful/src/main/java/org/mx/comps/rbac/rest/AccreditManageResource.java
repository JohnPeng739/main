package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.rest.vo.AccreditInfoVO;
import org.mx.comps.rbac.rest.vo.AccreditVO;
import org.mx.comps.rbac.service.AccreditManageService;
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
public class AccreditManageResource {
    private static final Log logger = LogFactory.getLog(AccreditManageResource.class);

    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    @Autowired
    private AccreditManageService accreditManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("accredits")
    @GET
    public DataVO<List<AccreditVO>> accredits() {
        try {
            List<Accredit> accredits = accessor.list(Accredit.class);
            List<AccreditVO> list = AccreditVO.transform(accredits);
            return new DataVO<>(list);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List accredits fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accredits")
    @POST
    public PaginationDataVO<List<AccreditVO>> accredits(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Accredit> accredits = accessor.list(pagination, Accredit.class);
            List<AccreditVO> list = AccreditVO.transform(accredits);
            return new PaginationDataVO<>(pagination, list);
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List accredits fail.", ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accredits/{id}")
    @GET
    public DataVO<AccreditVO> getAccredit(@PathParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            return new DataVO<>(new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        try {
            Accredit accredit = accessor.getById(id, Accredit.class);
            AccreditVO vo = AccreditVO.transform(accredit, true);
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Get accredit fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accredits/new")
    @POST
    public DataVO<AccreditVO> newAccredit(@QueryParam("userCode") String userCode, AccreditInfoVO accreditInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Accredit accredit = accreditManageService.accredit(accreditInfoVO.getAccreditInfo());
            AccreditVO vo = AccreditVO.transform(accredit, true);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Create accredit fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accredits/{id}")
    @DELETE
    public DataVO<AccreditVO> deleteAccredit(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        if (StringUtils.isBlank(userCode) || StringUtils.isBlank(id)) {
            return new DataVO<>(new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Accredit accredit = accreditManageService.closeAccredit(id);
            AccreditVO vo = AccreditVO.transform(accredit, true);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Delete accredit fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }
}
