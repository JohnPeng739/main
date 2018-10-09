package org.mx.tools.ffee.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.mx.service.rest.vo.DataVO;
import org.mx.spring.session.SessionDataStore;
import org.mx.tools.ffee.rest.bean.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("rest/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLResource {
    private static final Log logger = LogFactory.getLog(GraphQLResource.class);

    private GraphQLUtils graphQLUtils;
    private SessionDataStore sessionDataStore;

    @Autowired
    public GraphQLResource(GraphQLUtils graphQLUtils, SessionDataStore sessionDataStore) {
        super();
        this.graphQLUtils = graphQLUtils;
        this.sessionDataStore = sessionDataStore;
    }

    private void prepareRequest(String clientRequest) {
        if (!StringUtils.isBlank(clientRequest)) {
            try {
                ClientRequest request = JSON.parseObject(clientRequest, ClientRequest.class);
                Map<String, Object> map = sessionDataStore.get();
                map.put("currentUser", request.getAccountId());
                map.put("latitude", request.getLatitude());
                map.put("longitude", request.getLongitude());
                sessionDataStore.set(map);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Parse the json fail: %s.", clientRequest), ex);
                }
            }
        }
        if (StringUtils.isBlank(sessionDataStore.getCurrentUserCode())) {
            sessionDataStore.setCurrentUserCode("System");
        }
    }

    @Path("graph")
    @POST
    public DataVO<JSONObject> graphQL(@Context HttpHeaders headers, String request) {
        prepareRequest(headers.getHeaderString("Client-Request"));
        JSONObject result = graphQLUtils.execute(request);
        sessionDataStore.removeCurrentUserCode();
        return new DataVO<>(result);
    }
}
