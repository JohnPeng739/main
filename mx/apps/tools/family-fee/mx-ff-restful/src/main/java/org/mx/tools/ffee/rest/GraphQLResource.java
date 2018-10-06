package org.mx.tools.ffee.rest;

import com.alibaba.fastjson.JSONObject;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("rest/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLResource {
    private GraphQLUtils graphQLUtils;

    @Autowired
    public GraphQLResource(GraphQLUtils graphQLUtils) {
        super();
        this.graphQLUtils = graphQLUtils;
    }

    @Path("graph")
    @POST
    public DataVO<JSONObject> graphQL(String request) {
        JSONObject result = graphQLUtils.execute(request);
        return new DataVO<>(result);
    }
}
