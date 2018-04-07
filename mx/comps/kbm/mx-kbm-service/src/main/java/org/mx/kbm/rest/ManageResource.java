package org.mx.kbm.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.jwt.AuthenticateAround;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.session.SessionDataStore;
import org.mx.kbm.Constants;
import org.mx.kbm.entity.Category;
import org.mx.kbm.entity.KTag;
import org.mx.kbm.entity.Tenant;
import org.mx.kbm.error.UserInterfaceKmbErrorException;
import org.mx.kbm.rest.vo.CategoryVO;
import org.mx.kbm.rest.vo.MyCategoryVO;
import org.mx.kbm.rest.vo.MyKTagVO;
import org.mx.kbm.rest.vo.TenantVO;
import org.mx.kbm.service.CategoryService;
import org.mx.kbm.service.KTagService;
import org.mx.kbm.service.TenantService;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import java.util.List;

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
    @Qualifier("generalAccessor")
    private GeneralAccessor generalAccessor = null;

    @Autowired
    private TenantService tenantService = null;

    @Autowired
    private CategoryService categoryService = null;

    @Autowired
    private KTagService kTagService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Autowired
    private OperateLogService operateLogService = null;

    @Path("tenant")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<TenantVO> myTenant(@QueryParam("userCode") String userCode, @Context Request request) {
        Tenant tenant = tenantService.getTenantByAccount(userCode);
        return new DataVO<>(TenantVO.transform(tenant));
    }

    @Path("tenant")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<TenantVO> newTenant(@QueryParam("userCode") String userCode, TenantVO tenantVO,
                                      @Context Request request) {
        tenantVO.setId(null);
        return saveTenant(userCode, tenantVO);
    }

    @Path("tenant/{id}")
    @PUT
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<TenantVO> editTenant(@QueryParam("userCode") String userCode, @PathParam("id") String id,
                                       TenantVO tenantVO, @Context Request request) {
        tenantVO.setId(id);
        return saveTenant(userCode, tenantVO);
    }

    @Path("tenant/{id}")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<TenantVO> getTenant(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        Tenant tenant = generalAccessor.getById(id, Tenant.class);
        if (tenant == null) {
            return new DataVO<>(new UserInterfaceKmbErrorException(
                    UserInterfaceKmbErrorException.KmbErrors.TENANT_NOT_FOUND));
        } else {
            return new DataVO<>(TenantVO.transform(tenant));
        }
    }

    @Path("tenant/{id}")
    @DELETE
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<TenantVO> delTenant(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        setSessionData(Constants.SYSTEM, Constants.MODULE_MANAGE, userCode);
        Tenant tenant = generalAccessor.remove(id, Tenant.class);
        if (tenant == null) {
            return new DataVO<>(new UserInterfaceKmbErrorException(
                    UserInterfaceKmbErrorException.KmbErrors.TENANT_NOT_FOUND));
        } else {
            return new DataVO<>(TenantVO.transform(tenant));
        }
    }

    private void setSessionData(String system, String module, String userCode) {
        sessionDataStore.setCurrentSystem(system);
        sessionDataStore.setCurrentModule(module);
        sessionDataStore.setCurrentUserCode(userCode);
    }

    private DataVO<TenantVO> saveTenant(String userCode, TenantVO tenantVO) {
        setSessionData(Constants.SYSTEM, Constants.MODULE_MANAGE, userCode);
        Tenant tenant = tenantVO.transform();
        tenant = tenantService.saveTenant(tenant);
        sessionDataStore.clean();
        return new DataVO<>(TenantVO.transform(tenant));
    }

    @Path("categories")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<MyCategoryVO> myCategories(@QueryParam("userCode") String userCode, TenantVO tenantVO,
                                             @Context Request request) {
        List<Category> publicCategories = categoryService.getCategories(null);
        List<Category> privateCategories = null;
        if (tenantVO != null && !StringUtils.isBlank(tenantVO.getId())) {
            Tenant tenant = tenantVO.transform();
            privateCategories = categoryService.getCategories(tenant);
        }
        return new DataVO<>(new MyCategoryVO(publicCategories, privateCategories));
    }

    @Path("category")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<CategoryVO> newCategory(@QueryParam("userCode")String userCode, CategoryVO categoryVO,
                                          @Context Request request) {
        categoryVO.setId(null);
        return saveCategory(userCode, categoryVO);
    }

    @Path("category/{id}")
    @PUT
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<CategoryVO> editCategory(@QueryParam("userCode")String userCode, @PathParam("id") String id,
                                           CategoryVO categoryVO, @Context Request request) {
        categoryVO.setId(id);
        return saveCategory(userCode, categoryVO);
    }

    private DataVO<CategoryVO> saveCategory(String userCode, CategoryVO categoryVO) {
        setSessionData(Constants.SYSTEM, Constants.MODULE_MANAGE, userCode);
        Category category = categoryVO.transform();
        category = categoryService.saveCategory(category, categoryVO.getParentId());
        return new DataVO<>(CategoryVO.transform(category));
    }

    @Path("ktags")
    @POST
    public DataVO<MyKTagVO> myTags(@QueryParam("userCode") String userCode, TenantVO tenantVO,
                                   @Context Request request) {
        List<KTag> publicKTags = kTagService.getKTags(null);
        List<KTag> privateKTags = null;
        if (tenantVO != null && !StringUtils.isBlank(tenantVO.getId())) {
            Tenant tenant = tenantVO.transform();
            privateKTags = kTagService.getKTags(tenant);
        }
        return new DataVO<>(new MyKTagVO(publicKTags, privateKTags));
    }
}
