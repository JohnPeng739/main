package org.mx.comps.jwt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.glassfish.jersey.server.ContainerRequest;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Request;

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
    public Object aroundBefore(ProceedingJoinPoint pjp, AuthenticateAround around, Request request) throws Throwable {
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
    public Object aroundAfter(ProceedingJoinPoint pjp, AuthenticateAround around, Request request) throws Throwable {
        return around(pjp, around, request);
    }

    // 环绕截面
    private Object around(ProceedingJoinPoint pjp, AuthenticateAround around, Request request) throws Throwable {
        Object result = authenticate(pjp, around, request);
        if (result == null) {
            // 身份认证成功，调取代理方法
            result = pjp.proceed();
        }
        return result;
    }

    // 返回一个没有通过身份认证的结果
    private Object notAuthenticate(AuthenticateAround around) {
        Class<?> clazz = around.returnValueClass();
        if (clazz.isAssignableFrom(DataVO.class)) {
            return new DataVO<>(UserInterfaceRbacErrorException.RbacErrors.NOT_AUTHENTICATED);
        } else if (clazz.isAssignableFrom(PaginationDataVO.class)) {
            return new PaginationDataVO<>(UserInterfaceRbacErrorException.RbacErrors.NOT_AUTHENTICATED);
        } else {
            return "Not authenticate.";
        }
    }

    // 进行身份认证
    private Object authenticate(ProceedingJoinPoint pjp, AuthenticateAround around, Request request) {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting authenticate ....");
        }
        try {
            if (jwtService.verify((ContainerRequest) request)) {
                return null;
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("JWT verify fail.", ex);
            }
        }
        return notAuthenticate(around);
    }
}
