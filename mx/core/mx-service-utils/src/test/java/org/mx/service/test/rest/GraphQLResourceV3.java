package org.mx.service.test.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.service.rest.GraphQLBaseResource;
import org.mx.service.rest.graphql.GraphQLFactory;
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

@Path("rest/v3/graphql")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLResourceV3 extends GraphQLBaseResource {
    private static final Log logger = LogFactory.getLog(GraphQLResourceV3.class);
    private static final String schemaKey = "test";

    private SessionDataStore sessionDataStore;

    @Autowired
    public GraphQLResourceV3(GraphQLFactory factory, SessionDataStore sessionDataStore) {
        super(factory);
        this.sessionDataStore = sessionDataStore;
    }

    @Path("mutation")
    @POST
    public DataVO<JSONObject> mutation2(@Context HttpHeaders headers, JSON request) {
        DataVO<JSONObject> result = super.mutation(schemaKey, request);
        sessionDataStore.removeCurrentUserCode();
        return result;
    }

    @Path("query")
    @POST
    public DataVO<JSONObject> query2(@Context HttpHeaders headers,  JSON request) {
        DataVO<JSONObject> result = super.query(schemaKey, request);
        sessionDataStore.removeCurrentUserCode();
        return result;
    }
}
