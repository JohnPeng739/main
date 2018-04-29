package org.mx.kbm.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.kbm.Constants;
import org.mx.kbm.entity.KnowledgeContact;
import org.mx.kbm.entity.KnowledgeTenant;
import org.mx.kbm.error.UserInterfaceKbmErrorException;
import org.mx.kbm.rest.vo.ContactVO;
import org.mx.kbm.rest.vo.TenantRegisterRequestVO;
import org.mx.kbm.rest.vo.TenantVO;
import org.mx.kbm.service.ContactService;
import org.mx.kbm.service.TenantService;
import org.mx.kbm.service.bean.ContactRegisterRequest;
import org.mx.kbm.service.bean.TenantRegisterRequest;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 描述： KBM基础数据Restful服务
 *
 * @author John.Peng
 *         Date time 2018/3/26 下午2:05
 */
@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManageResource {
    private static final Log logger = LogFactory.getLog(ManageResource.class);

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Autowired
    private TenantService tenantService = null;

    @Autowired
    private ContactService contactService = null;

    public ManageResource() {
        super();
        sessionDataStore.setCurrentSystem(Constants.SYSTEM);
        sessionDataStore.setCurrentModule(Constants.MODULE_MANAGE);
    }

    @Path("tenants/register")
    @POST
    public DataVO<TenantVO> tenantRegister(@QueryParam("userCode") String userCode,
                                           TenantRegisterRequestVO registerRequestVO) {
        try {
            sessionDataStore.setCurrentUserCode(userCode);
            TenantRegisterRequest registerRequest = registerRequestVO.get();
            KnowledgeTenant tenant = tenantService.register(registerRequest);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(TenantVO.transform(tenant));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Tenant register fail.", ex);
            }
            return new DataVO<>(new UserInterfaceKbmErrorException(
                    UserInterfaceKbmErrorException.KbmErrors.OTHER_FAIL));
        }
    }

    @Path("contacts/register")
    @POST
    public DataVO<ContactVO> contactRegister(@QueryParam("userCode") String userCode,
                                             ContactRegisterRequest registerRequest) {
        try {
            sessionDataStore.setCurrentModule(userCode);
            KnowledgeContact contact = contactService.register(registerRequest);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(ContactVO.transform(contact));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Tenant register fail.", ex);
            }
            return new DataVO<>(new UserInterfaceKbmErrorException(
                    UserInterfaceKbmErrorException.KbmErrors.OTHER_FAIL));
        }
    }
}
