package org.mx.kbm.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.session.SessionDataStore;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 描述： KBM基础数据Restful服务
 *
 * @author John.Peng
 *         Date time 2018/3/26 下午2:05
 */
@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManageResource {
    private static final Log logger = LogFactory.getLog(ManageResource.class);

    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor generalAccessor = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Autowired
    private OperateLogService operateLogService = null;

    @Path("test")
    @GET
    public DataVO<Boolean> test() {
        // TODO
        return null;
    }
}
