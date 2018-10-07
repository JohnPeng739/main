package org.mx.tools.ffee.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.mx.service.rest.vo.DataVO;
import org.mx.spring.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

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
                if (!StringUtils.isBlank(request.getAccountCode())) {
                    sessionDataStore.setCurrentUserCode(request.getAccountCode());
                }
                sessionDataStore.set("longitude", request.getLongitude());
                sessionDataStore.set("latitude", request.getLatitude());
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Parse the json fail: %s.", clientRequest));
                }
            }
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

    private class ClientRequest {
        private String accountCode;
        private double latitude, longitude;

        public String getAccountCode() {
            return accountCode;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setAccountCode(String accountCode) {
            this.accountCode = accountCode;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
