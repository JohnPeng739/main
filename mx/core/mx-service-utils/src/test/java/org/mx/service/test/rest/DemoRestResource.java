package org.mx.service.test.rest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.auth.RestAuthenticate;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created by john on 2017/11/4.
 */
@Component
@Path("service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DemoRestResource {
    @Autowired
    private Environment env = null;

    @Path("get")
    @GET
    public DataVO<String> get() {
        System.out.println(env.getProperty("restful.port"));
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

    @Path("exception/system")
    @GET
    public DataVO<String> systemException() {
        throw new UserInterfaceSystemErrorException(
                UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
        );
    }

    @Path("exception")
    @GET
    public DataVO<Boolean> exception() {
        throw new RuntimeException("Any exception.");
    }

    @Path("authenticate")
    @GET
    @RestAuthenticate
    public DataVO<String> authenticate() {
        return new DataVO<>("ok");
    }

    @Path("upload")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public DataVO<String> upload(@FormDataParam("file") InputStream in,
                                 @FormDataParam("file")FormDataContentDisposition detail) {
        System.out.println(detail.getFileName());
        System.out.println(detail.getSize());
        System.out.println(detail.getType());
        return new DataVO<>(detail.getFileName());
    }
}
