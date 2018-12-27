package org.mx.service.test.rest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.service.rest.GraphQLBaseResource;
import org.mx.service.rest.graphql.GraphQLFactory;
import org.mx.service.rest.graphql.GraphQLRequest;
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

@Path("rest/v1/graphql")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLResource extends GraphQLBaseResource {
    private static final Log logger = LogFactory.getLog(GraphQLResource.class);
    private static final String schemaKey = "test";

    private SessionDataStore sessionDataStore;

    @Autowired
    public GraphQLResource(GraphQLFactory factory, SessionDataStore sessionDataStore) {
        super(factory);
        this.sessionDataStore = sessionDataStore;
    }

    @Path("mutation")
    @POST
    public DataVO<JSONObject> mutation(@Context HttpHeaders headers, GraphQLRequest request) {
        DataVO<JSONObject> result = super.mutation(schemaKey, request);
        sessionDataStore.removeCurrentUserCode();
        return result;
    }

    @Path("query")
    @POST
    public DataVO<JSONObject> query(@Context HttpHeaders headers, GraphQLRequest request) {
        DataVO<JSONObject> result = super.query(schemaKey, request);
        sessionDataStore.removeCurrentUserCode();
        return result;
    }
}
