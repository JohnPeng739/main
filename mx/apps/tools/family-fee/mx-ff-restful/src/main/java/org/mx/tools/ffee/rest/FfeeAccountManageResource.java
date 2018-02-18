package org.mx.tools.ffee.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.error.UserInterfaceException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.rest.vo.FfeeAccountVO;
import org.mx.tools.ffee.rest.vo.FfeeRegistInfoVO;
import org.mx.tools.ffee.service.FfeeAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 描述： FFEE账户管理Restful服务
 *
 * @author: John.Peng
 * @date: 2018/2/18 下午4:54
 */
@Component
@Path("rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FfeeAccountManageResource {
    private static final Log logger = LogFactory.getLog(FfeeAccountManageResource.class);

    @Autowired
    private FfeeAccountService ffeeAccountService = null;

    @Path("regist")
    @POST
    public DataVO<FfeeAccountVO> regist(FfeeRegistInfoVO registInfoVO) {
        try {
            FfeeAccount ffeeAccount = ffeeAccountService.regist(registInfoVO.getCode(), registInfoVO.getName(),
                    registInfoVO.getPassword());
            return new DataVO<>(FfeeAccountVO.transform(ffeeAccount));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        }
    }
}
