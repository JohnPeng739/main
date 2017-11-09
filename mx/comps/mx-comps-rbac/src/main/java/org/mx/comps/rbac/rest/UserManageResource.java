package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.rest.vo.UserVO;
import org.mx.dal.exception.EntityAccessException;
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
import java.util.List;

@Component
@Path("/rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserManageResource {
    private static final Log logger = LogFactory.getLog(UserManageResource.class);

    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralEntityAccessor accessor;

    @Path("users")
    @GET
    public DataVO<UserVO> listUsers() {
        try {
            List<User> users = accessor.list(User.class);
            return null;
        } catch (EntityAccessException ex) {
            //
        }
    }
}
