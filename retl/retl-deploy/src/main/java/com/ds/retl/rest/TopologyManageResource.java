package com.ds.retl.rest;

import com.ds.retl.dal.entity.Topology;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.jms.JmsManager;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.rest.vo.LabelValueVO;
import com.ds.retl.rest.vo.topology.SupportedVO;
import com.ds.retl.rest.vo.topology.TopologyVO;
import com.ds.retl.service.TopologyManageService;
import com.ds.retl.validate.TypeValidateFunc;
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
import java.util.Collections;
import java.util.List;

/**
 * RETL计算拓扑管理RESTful服务资源类
 *
 * @author : john.peng created on date : 2017/9/24
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

    /**
     * 按页列举RETL计算拓扑列表
     *
     * @param pagination 分页对象
     * @return 符合条件的计算拓扑列表
     */
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

    /**
     * 提交输入的拓扑配置信息到Storm集群中
     *
     * @param userCode              操作用户代码
     * @param topologyId            拓扑关键字ID，如果是新增，则为null
     * @param topologyConfigJsonStr 计算拓扑配置信息
     * @return 提交成功返回提交的拓扑对象，否则返回错误信息。
     */
    @Path("topology/submit")
    @POST
    public DataVO<TopologyVO> submitTopology(@QueryParam("userCode") String userCode,
                                             @QueryParam("topologyId") String topologyId,
                                             String topologyConfigJsonStr) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Topology topology = topologyManageService.submit(topologyId, topologyConfigJsonStr);
            TopologyVO topologyVO = new TopologyVO();
            TopologyVO.transform(topology, topologyVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(topologyVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    /**
     * 保存输入的拓扑配置信息
     *
     * @param userCode              操作用户代码
     * @param topologyId            拓扑关键字ID，如果是新增，则为null
     * @param topologyConfigJsonStr 计算拓扑配置信息
     * @return 保存成功返回提交的拓扑对象，否则返回错误信息。
     */
    @Path("topology/save")
    @POST
    public DataVO<TopologyVO> saveTopology(@QueryParam("userCode") String userCode,
                                           @QueryParam("topologyId") String topologyId,
                                           String topologyConfigJsonStr) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Topology topology = topologyManageService.save(topologyId, topologyConfigJsonStr);
            TopologyVO topologyVO = new TopologyVO();
            TopologyVO.transform(topology, topologyVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(topologyVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    /**
     * 提交指定关键字ID的计算拓扑到Storm集群中
     *
     * @param userCode   操作用户代码
     * @param simulation 设置为true表示本地仿真，否则真正提交集群
     * @param id         拓扑的关键字ID
     * @return 提交成功返回提交的拓扑对象，否则返回错误信息。
     */
    @Path("topology/submit/{id}")
    @GET
    public DataVO<TopologyVO> submitTopology(@QueryParam("userCode") String userCode,
                                             @QueryParam("simulation") boolean simulation,
                                             @PathParam("id") String id) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Topology topology = topologyManageService.submit(id, simulation);
            TopologyVO topologyVO = new TopologyVO();
            TopologyVO.transform(topology, topologyVO);
            return new DataVO<>(topologyVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    /**
     * 校验定义的资源是否有效
     *
     * @param type            校验类型
     * @param resourceJsonStr 资源定义配置信息
     * @return 如果校验通过返回true，否则返回错误信息。
     */
    @Path("topology/validate")
    @POST
    public DataVO<Boolean> validateResource(@QueryParam("type") String type, String resourceJsonStr) {
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

    /**
     * 将符合条件的类信息转换为Label-Value值对象
     *
     * @param classes 类名列表
     * @return 成功返回Label-Value值对象列表，否则返回空列表。
     */
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
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("Fetch class[%s] info fail.", className));
                    }
                }
            });
        }
        return supported;
    }

    /**
     * 获取系统中相关的各种支持列表信息
     *
     * @return 支持信息列表
     */
    @Path("topology/supported")
    @GET
    public DataVO<SupportedVO> getSupported() {
        SupportedVO supportedVO = new SupportedVO();
        // get JMS supported
        List<LabelValueVO> supportedJms = new ArrayList<>();
        for (JmsManager.Supported s : JmsManager.Supported.values()) {
            supportedJms.add(new LabelValueVO(s.name(), s.name()));
        }
        Collections.sort(supportedJms);
        supportedVO.setJmsTypes(supportedJms);

        // get validate types
        List<String> validateClasses = ClassUtils.scanPackage("com.ds.retl.validate");
        List<LabelValueVO> validateTypes = this.transform(validateClasses);
        Collections.sort(validateTypes);
        supportedVO.setValidateTypes(validateTypes);

        // get validate rule types
        TypeValidateFunc.ValueType[] types = TypeValidateFunc.ValueType.values();
        List<LabelValueVO> supportedValidateRules = new ArrayList<>();
        for (TypeValidateFunc.ValueType type : types) {
            switch (type) {
                case STRING:
                    supportedValidateRules.add(new LabelValueVO("1. 字符串类型", type.name()));
                    break;
                case DATE:
                    supportedValidateRules.add(new LabelValueVO("2. 时间类型", type.name()));
                    break;
                case INT:
                    supportedValidateRules.add(new LabelValueVO("3. 整数", type.name()));
                    break;
                case DECIMAL:
                    supportedValidateRules.add(new LabelValueVO("4. 小数类型", type.name()));
                    break;
                case BOOL:
                    supportedValidateRules.add(new LabelValueVO("5. 布尔类型", type.name()));
                    break;
                default:
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("Unsupported type: %s.", type));
                    }
            }
        }
        Collections.sort(supportedValidateRules);
        supportedVO.setValidateRuleTypes(supportedValidateRules);

        // get transform types
        List<String> transformClasses = ClassUtils.scanPackage("com.ds.retl.transform");
        List<LabelValueVO> transformTypes = this.transform(transformClasses);
        Collections.sort(transformTypes);
        supportedVO.setTransformTypes(transformTypes);

        return new DataVO<>(supportedVO);
    }
}
