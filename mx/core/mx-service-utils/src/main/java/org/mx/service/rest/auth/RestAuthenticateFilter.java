package org.mx.service.rest.auth;

import com.auth0.jwt.interfaces.Claim;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceError;
import org.mx.jwt.service.JwtService;
import org.mx.service.error.UserInterfaceServiceErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.spring.utils.SpringContextHolder;
import org.springframework.core.env.Environment;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 描述： RESTful服务身份鉴别的拦截器定义
 *
 * @author john peng
 * Date time 2018/8/26 下午8:09
 */
@Provider
@RestAuthenticate
@Priority(Priorities.AUTHENTICATION)
public class RestAuthenticateFilter implements ContainerRequestFilter {
    private static final Log logger = LogFactory.getLog(RestAuthenticateFilter.class);

    /**
     * 根据输入的RESTful查询参数（Query Parameter）创建额外的鉴别条件
     *
     * @param queryParameters 查询参数
     * @return 额外的鉴别条件
     */
    private Predicate<Map<String, Claim>> extTokenVerify(MultivaluedMap<String, String> queryParameters) {
        if (queryParameters == null || queryParameters.isEmpty()) {
            return null;
        }
        Environment env = SpringContextHolder.getBean(Environment.class);
        String accountCode = "accountCode";
        if (env != null) {
            accountCode = env.getProperty("restful.token.account.query.field", "accountCode");
        }
        if (queryParameters.containsKey(accountCode)) {
            return JwtService.JwtVerifyPredicateBuilder.eq("accountCode", queryParameters.get(accountCode).get(0));
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("There are not query parameter[%s].", accountCode));
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ContainerRequestFilter#filter(ContainerRequestContext)
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        // 从请求头中获取token
        String token = requestContext.getHeaderString("token");
        if (StringUtils.isBlank(token)) {
            token = requestContext.getHeaderString("Authorization");
        }
        if (StringUtils.isBlank(token)) {
            // 无效的令牌
            requestContext.abortWith(Response.ok(
                    new DataVO((UserInterfaceError) new UserInterfaceServiceErrorException(
                            UserInterfaceServiceErrorException.ServiceErrors.TOKEN_INVALID)
                    )).build());
            return;
        }
        JwtService jwtService = SpringContextHolder.getBean(JwtService.class);
        if (jwtService == null) {
            // JWT服务未就绪
            requestContext.abortWith(Response.ok(
                    new DataVO((UserInterfaceError) new UserInterfaceServiceErrorException(
                            UserInterfaceServiceErrorException.ServiceErrors.JWT_SERVICE_NOT_INIT)
                    )).build());
            return;
        }
        JwtService.JwtVerifyResult result = jwtService.verifyToken(token, extTokenVerify(
                requestContext.getUriInfo().getQueryParameters()
        ));
        if (!result.isPassed()) {
            // 令牌校验不通过
            requestContext.abortWith(Response.ok(
                    new DataVO((UserInterfaceError) new UserInterfaceServiceErrorException(
                            UserInterfaceServiceErrorException.ServiceErrors.TOKEN_INVALID)
                    )).build());
        }
    }
}
