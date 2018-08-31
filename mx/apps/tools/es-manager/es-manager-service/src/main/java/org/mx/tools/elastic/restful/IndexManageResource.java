package org.mx.tools.elastic.restful;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.utils.ElasticLowLevelUtil;
import org.mx.dal.utils.bean.IndicesInfoBean;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.auth.RestAuthenticate;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Component
@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexManageResource {
    private static final Log logger = LogFactory.getLog(IndexManageResource.class);

    private ElasticLowLevelUtil elasticLowLevelUtil;

    @Autowired
    public IndexManageResource(ElasticLowLevelUtil elasticLowLevelUtil) {
        super();
        this.elasticLowLevelUtil = elasticLowLevelUtil;
    }

    @RestAuthenticate
    @Path("indexes")
    @GET
    public DataVO<List<IndicesInfoBean>> getAllIndexes() {
        return new DataVO<>(elasticLowLevelUtil.getAllIndexes());
    }

    @SuppressWarnings("unchecked")
    @RestAuthenticate
    @Path("indexes/new")
    @POST
    public DataVO<List<IndicesInfoBean>> createIndex(JSONObject indexInfoJSON) {
        String index = indexInfoJSON.getString("index");
        if (StringUtils.isBlank(index)) {
            if (logger.isErrorEnabled()) {
                logger.error("The index's name is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Map<String, Object> settings = indexInfoJSON.getObject("settings", Map.class);
        Map<String, Object> properties = indexInfoJSON.getObject("properties", Map.class);
        elasticLowLevelUtil.createIndex(index, settings, properties);
        return new DataVO<>(elasticLowLevelUtil.getAllIndexes());
    }

    @RestAuthenticate
    @Path("indexes/{index}")
    @DELETE
    public DataVO<Boolean> deleteIndex(@PathParam("index") String index) {
        elasticLowLevelUtil.deleteIndex(index);
        return new DataVO<>(true);
    }
}
