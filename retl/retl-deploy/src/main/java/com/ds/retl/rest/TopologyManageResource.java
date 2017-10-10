package com.ds.retl.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.dal.entity.Topology;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import com.ds.retl.jms.JmsManager;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.rest.vo.LabelValueVO;
import com.ds.retl.rest.vo.topology.TopologyVO;
import com.ds.retl.rest.vo.topology.TypesVO;
import com.ds.retl.service.TopologyManageService;
import com.ds.retl.validate.TypeValidateFunc;
import net.bytebuddy.asm.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.ClassUtils;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/9/24.
 */

@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopologyManageResource {
    private static final Log logger = LogFactory.getLog(TopologyManageResource.class);

    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralAccessor accessor = null;

    @Autowired
    private TopologyManageService topologyManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("topologies")
    @POST
    public PaginationDataVO<List<TopologyVO>> pagingList(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Topology> topologies = accessor.list(pagination, Topology.class);
            List<TopologyVO> list = new ArrayList<>();
            if (topologies != null && !topologies.isEmpty()) {
                topologies.forEach(topology -> {
                    TopologyVO vo = new TopologyVO();
                    TopologyVO.transform(topology, vo);
                    list.add(vo);
                });
            }
            return new PaginationDataVO<>(pagination, list);
        } catch (EntityAccessException ex) {
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("topology/submit")
    @POST
    public DataVO<Boolean> submitTopology(@QueryParam("userCode") String userCode, String topologyConfigJsonStr) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            JSONObject json = JSON.parseObject(topologyConfigJsonStr);
            topologyManageService.submit(json.getString("name"), topologyConfigJsonStr);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(true);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("topology/validate")
    @POST
    public DataVO<Boolean> validateResource(@QueryParam("type")String type, String resourceJsonStr) {
        try {
            boolean validated = false;
            switch (type) {
                case "zookeepers":
                    validated = topologyManageService.validateZookeepers(resourceJsonStr);
                    break;
                case "jdbcDataSource":
                    validated = topologyManageService.validateJdbcDataSource(resourceJsonStr);
                    break;
                case "jmsDataSource":
                    validated = topologyManageService.validateJmsDataSource(resourceJsonStr);
                    break;
                default:
                    throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
            }
            return new DataVO<>(validated);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_VALIDATE_FAIL));
        }
    }

    private List<LabelValueVO> transform(List<String> classes) {
        List<LabelValueVO> supported = new ArrayList<>();
        if (classes != null && !classes.isEmpty()) {
            classes.forEach(className -> {
                try {
                    Class<?> clazz = Class.forName(className);
                    Field codeField = clazz.getDeclaredField("CODE");
                    Field nameField = clazz.getDeclaredField("NAME");
                    String code = null, name = null;
                    if (codeField != null) {
                        code = (String) codeField.get(clazz);
                    }
                    if (nameField != null) {
                        name = (String) nameField.get(clazz);
                    }
                    if (!StringUtils.isBlank(code) && !StringUtils.isBlank(name)) {
                        supported.add(new LabelValueVO(name, code));
                    }
                } catch (Exception ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Fetch class[%s] info fail.", className));
                    }
                }
            });
        }
        return supported;
    }

    @Path("topology/types")
    @GET
    public DataVO<TypesVO> getTypes() {
        return new DataVO<>(new TypesVO());
    }

    @Path("topology/jms/supported")
    @GET
    public DataVO<List<String>> supportedJms() {
        List<String> supported = new ArrayList<>();
        for (JmsManager.Supported s : JmsManager.Supported.values()) {
            supported.add(s.name());
        }
        return new DataVO<>(supported);
    }

    @Path("topology/validates/supported")
    @GET
    public DataVO<List<LabelValueVO>> supportedValidates() {
        List<String> validateClasses = ClassUtils.scanPackage("com.ds.retl.validate");
        List<LabelValueVO> supported = this.transform(validateClasses);
        return new DataVO<>(supported);
    }

    @Path("topology/validate/type-validate/types")
    @GET
    public DataVO<List<LabelValueVO>> supportedValidateTypes() {
        TypeValidateFunc.ValueType[] types = TypeValidateFunc.ValueType.values();
        List<LabelValueVO> supported = new ArrayList<>();
        for (TypeValidateFunc.ValueType type : types) {
            switch (type) {
                case STRING:
                    supported.add(new LabelValueVO("字符串类型", type.name()));
                    break;
                case DATE:
                    supported.add(new LabelValueVO("时间类型", type.name()));
                    break;
                case INT:
                    supported.add(new LabelValueVO("整数", type.name()));
                    break;
                case DECIMAL:
                    supported.add(new LabelValueVO("小数类型", type.name()));
                    break;
                case BOOL:
                    supported.add(new LabelValueVO("布尔类型", type.name()));
                    break;
                default:
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("Unsupported type: %s.", type));
                    }
            }
        }
        return new DataVO<>(supported);
    }

    @Path("topology/transforms/supported")
    @GET
    public DataVO<List<LabelValueVO>> supportedTransforms() {
        List<String> transformClasses = ClassUtils.scanPackage("com.ds.retl.transform");
        List<LabelValueVO> supported = this.transform(transformClasses);
        return new DataVO<>(supported);
    }
}
