package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.rest.vo.AccreditVO;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
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
    @Qualifier("generalDictEntityAccessorHibernate")
    private GeneralDictAccessor accessor = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("accredits")
    @GET
    public DataVO<List<AccreditVO>> accredits() {
        try {
            List<Accredit> accredits = accessor.list(Accredit.class);
            List<AccreditVO> list = AccreditVO.transformAccreditVOs(accredits);
            return new DataVO<>(list);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
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
            List<AccreditVO> list = AccreditVO.transformAccreditVOs(accredits);
            return new PaginationDataVO<>(pagination, list);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("accredits/{id}")
    @GET
    public DataVO<AccreditVO> getAccredit(@QueryParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM));
        }
        try {
            Accredit accredit = accessor.getById(id, Accredit.class);
            AccreditVO vo = new AccreditVO();
            AccreditVO.transform(accredit, vo);
            return new DataVO<>(vo);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("accredits/new")
    @POST
    public DataVO<AccreditVO> newAccredit(@QueryParam("userCode") String userCode, AccreditVO accreditVO) {
        if (StringUtils.isBlank(userCode) || accreditVO == null) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM));
        }
        try {
            Accredit accredit = EntityFactory.createEntity(Accredit.class);
            AccreditVO.transform(accreditVO, accredit);
            accredit = accessor.save(accredit);
            AccreditVO vo = new AccreditVO();
            AccreditVO.transform(accredit, vo);
            return new DataVO<>(vo);
        } catch (EntityInstantiationException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.ENTITY_INSTANCE_FAIL));
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("accredits/{id}")
    @DELETE
    public DataVO<AccreditVO> deleteAccredit(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        if (StringUtils.isBlank(userCode) || StringUtils.isBlank(id)) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM));
        }
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Accredit accredit = accessor.remove(id, Accredit.class);
            AccreditVO vo = new AccreditVO();
            AccreditVO.transform(accredit, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }
}
