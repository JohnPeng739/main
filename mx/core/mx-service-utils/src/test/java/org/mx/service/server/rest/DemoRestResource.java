package org.mx.service.server.rest;

import org.mx.service.rest.vo.DataVO;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by john on 2017/11/4.
 */
@Component
@Path("service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DemoRestResource {
    @Path("get")
    @GET
    public DataVO<String> get() {
        return new DataVO<>("get data.");
    }

    @Path("post")
    @POST
    public DataVO<String> post(String data) {
        return new DataVO<>(String.format("post data: %s.", data));
    }

    @Path("put")
    @PUT
    public DataVO<String> put(String data) {
        return new DataVO<>(String.format("put data: %s.", data));
    }
}
