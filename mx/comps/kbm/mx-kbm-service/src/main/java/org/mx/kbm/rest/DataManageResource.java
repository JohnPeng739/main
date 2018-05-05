package org.mx.kbm.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.jwt.AuthenticateAround;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.kbm.Constants;
import org.mx.kbm.entity.KnowledgeContact;
import org.mx.kbm.entity.KnowledgeTenant;
import org.mx.kbm.error.UserInterfaceKbmErrorException;
import org.mx.kbm.rest.vo.*;
import org.mx.kbm.service.CategoryService;
import org.mx.kbm.service.ContactService;
import org.mx.kbm.service.TenantService;
import org.mx.kbm.service.bean.CategoryTreeBean;
import org.mx.kbm.service.bean.ContactDetailsBean;
import org.mx.kbm.service.bean.ContactRegisterRequest;
import org.mx.kbm.service.bean.TenantRegisterRequest;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import java.util.List;

/**
 * 描述： KBM基础数据Restful服务
 *
 * @author John.Peng
 * Date time 2018/3/26 下午2:05
 */
@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DataManageResource {
    private static final Log logger = LogFactory.getLog(DataManageResource.class);

    private SessionDataStore sessionDataStore;
    private TenantService tenantService;
    private ContactService contactService;
    private CategoryService categoryService;

    /**
     * 默认的构造函数
     *
     * @param sessionDataStore 会话数据存储服务接口
     * @param tenantService    租户服务接口
     * @param contactService   联系人服务接口
     * @param categoryService  分类服务接口
     */
    @Autowired
    public DataManageResource(SessionDataStore sessionDataStore, TenantService tenantService,
                              ContactService contactService, CategoryService categoryService) {
        super();
        this.sessionDataStore = sessionDataStore;
        this.tenantService = tenantService;
        this.contactService = contactService;
        this.categoryService = categoryService;
    }

    /**
     * 设置本模块中的session data
     *
     * @param userCode 当前操作用户代码
     */
    private void setSessionData(String userCode) {
        sessionDataStore.setCurrentSystem(Constants.SYSTEM);
        sessionDataStore.setCurrentModule(Constants.MODULE_MANAGE);
        sessionDataStore.setCurrentUserCode(userCode);
    }

    @Path("tenants/register")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<TenantVO> tenantRegister(@QueryParam("userCode") String userCode,
                                           TenantRegisterRequestVO registerRequestVO, @Context Request request) {
        try {
            setSessionData(userCode);
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
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<ContactVO> contactRegister(@QueryParam("userCode") String userCode,
                                             ContactRegisterRequest registerRequest, @Context Request request) {
        try {
            setSessionData(userCode);
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

    @Path("contacts/{contactId}")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<ContactDetailsVO> getContact(@PathParam("contactId") String contactId) {
        try {
            ContactDetailsBean contactDetailsBean = contactService.getContactDetailsById(contactId);
            return new DataVO<>(ContactDetailsVO.transform(contactDetailsBean));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Fetch the contact's details information fail, id: %s.", contactId), ex);
            }
            return new DataVO<>(new UserInterfaceKbmErrorException(
                    UserInterfaceKbmErrorException.KbmErrors.OTHER_FAIL));
        }
    }

    @Path("categories")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<List<CategoryTreeVO>> getCategories(@QueryParam("rootCategoryId") String rootCategoryId) {
        try {
            List<CategoryTreeBean> categoryTreeBeans = categoryService.getCategoryByParentId(rootCategoryId);
            return new DataVO<>(CategoryTreeVO.transform(categoryTreeBeans));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Fetch the categories' details information fail, id: %s.", rootCategoryId), ex);
            }
            return new DataVO<>(new UserInterfaceKbmErrorException(
                    UserInterfaceKbmErrorException.KbmErrors.OTHER_FAIL));
        }
    }
}
