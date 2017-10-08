package com.ds.retl.rest;

import com.ds.retl.rest.vo.LabelValueVO;
import com.ds.retl.validate.TypeValidateFunc;
import net.bytebuddy.asm.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.ClassUtils;
import org.mx.StringUtils;
import org.mx.dal.session.SessionDataStore;
import org.mx.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
    private SessionDataStore sessionDataStore = null;

    @Path("topologies/submit")
    @POST
    public DataVO<Boolean> submitTopology(String topologyConfigJsonStr, @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        // TODO
        System.out.println(topologyConfigJsonStr);
        sessionDataStore.removeCurrentUserCode();
        return new DataVO<>(true);
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
