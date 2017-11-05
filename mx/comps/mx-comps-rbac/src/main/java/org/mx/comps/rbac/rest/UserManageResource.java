package org.mx.comps.rbac.rest;

import org.mx.comps.rbac.rest.vo.UserVO;
import org.mx.dal.service.GeneralEntityAccessor;
import org.mx.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path("/rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserManageResource {
    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralEntityAccessor accessor;

    @Path("users")
    @GET
    public DataVO<UserVO> listUsers() {
        return null;
    }
}
