package org.mx.tools.ffee.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.service.client.rest.RestClientInvoke;
import org.mx.service.client.rest.RestInvokeException;
import org.mx.tools.ffee.config.FfeeConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 描述： 用于微信相关验证的RESTful资源服务类定义
 *
 * @author john peng
 * Date time 2018/9/10 下午6:46
 */
@Component("wxAuthorizationResource")
@Path("rest/v1/wx")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WxAuthorizationResource {
    private static final Log logger = LogFactory.getLog(WxAuthorizationResource.class);

    private FfeeConfigBean ffeeConfigBean;

    @Autowired
    public WxAuthorizationResource(FfeeConfigBean ffeeConfigBean) {
        super();
        this.ffeeConfigBean = ffeeConfigBean;
    }

    @Path("echo")
    @GET
    public String wxEcho(@QueryParam("signature") String signature, @QueryParam("timestamp") String timestamp,
                         @QueryParam("nonce") String nonce, @QueryParam("echostr") String echostr) {
        String token = ffeeConfigBean.getToken();
        List<String> list = Arrays.asList(timestamp, nonce, token);
        Collections.sort(list);
        String checked = DigestUtils.sha1(StringUtils.merge(list, ""), DigestUtils.EncodeType.HEX);
        logger.info(String.format("signature: %s\nchecked: %s\ntimestamp: %s\nnonce: %s\nechostr: %s",
                signature, checked, timestamp, nonce, echostr));
        return signature.equals(checked) ? echostr : "";
    }

    @Path("decode")
    @GET
    public String jsDecode(@QueryParam("code") String code) {
        String appId = ffeeConfigBean.getAppId(), appSecurity = ffeeConfigBean.getSecurity(),
                decodeUrl = ffeeConfigBean.getApiUrl() +
                        "?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

        String url = String.format(decodeUrl, appId, appSecurity, code);
        try {
            RestClientInvoke invoke = new RestClientInvoke();
            String result = invoke.get(url, String.class);
            logger.info(String.format("decode: %s.", result));
            return result;
        } catch (RestInvokeException ex) {
            logger.error("decode fail.", ex);
            return null;
        }
    }
}
