package org.mx.service.server.rest;

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
    public String get() {
        return "get data.";
    }

    @Path("post")
    @POST
    public String post(String data) {
        return String.format("post data: %s.", data);
    }

    @Path("put")
    @PUT
    public String put(String data) {
        return String.format("put data: %s.", data);
    }
}
