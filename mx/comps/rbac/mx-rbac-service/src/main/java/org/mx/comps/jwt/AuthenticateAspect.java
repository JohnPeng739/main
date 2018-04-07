package org.mx.comps.jwt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.glassfish.jersey.server.ContainerRequest;
import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;

/**
 * 身份认证自动代理切面
 *
 * @author : john.peng created on date : 2018/1/16
 */
@Component
@Aspect
public class AuthenticateAspect {
    private static final Log logger = LogFactory.getLog(AuthenticateAspect.class);

    @Autowired
    private JwtService jwtService = null;

    /**
     * Request参数在最后
     *
     * @param pjp     拦截点
     * @param around  环绕注解定义
     * @param request Request
     * @return 处理结果
     * @throws Throwable 处理过程中发生的异常
     */
    @Around(value = "@annotation(around) && args(..,request)")
    public Object aroundBefore(ProceedingJoinPoint pjp, AuthenticateAround around, Object request) throws Throwable {
        return around(pjp, around, request);
    }

    /**
     * Request参数在最前
     *
     * @param pjp     拦截点
     * @param around  环绕注解定义
     * @param request Request
     * @return 处理结果
     * @throws Throwable 处理过程中发生的异常
     */
    @Around(value = "@annotation(around) && args(request,..)")
    public Object aroundAfter(ProceedingJoinPoint pjp, AuthenticateAround around, Object request) throws Throwable {
        return around(pjp, around, request);
    }

    // 环绕截面
    private Object around(ProceedingJoinPoint pjp, AuthenticateAround around, Object request) throws Throwable {
        authenticate(pjp, around, request);
        // 身份认证成功，调取代理方法
        return pjp.proceed();
    }

    // 进行身份认证
    private void authenticate(ProceedingJoinPoint pjp, AuthenticateAround around, Object request) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting authenticate ....");
        }
        String token = "";
        try {
            if (request instanceof ContainerRequest) {
                token = ((ContainerRequest) request).getHeaderString("token");
                if (StringUtils.isBlank(token)) {
                    token = ((ContainerRequest) request).getHeaderString("Authorization");
                }
            } else if (request instanceof ServletRequest) {
                token = ((ServletRequest) request).getParameter("token");
                if (StringUtils.isBlank(token)) {
                    token = ((ServletRequest) request).getParameter("Authorization");
                }
            } else {
                String message = String.format("Unsupported verify request object type: %s.",
                        request.getClass().getName());
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new TokenVerifyException(message);
            }

            if (StringUtils.isBlank(token)) {
                String message = "The token is not existed in the request object.";
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                throw new TokenVerifyException(message);
            }
            if (!StringUtils.isBlank(token) && token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length());
            }
            if (jwtService.verify(token)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("The token[%s] verify passed.", token));
                }
            }
        } catch (Exception ex) {
            String message = String.format("The token[%s] verify fail.", token);
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
            throw new TokenVerifyException(message, ex);
        }
    }
}
