package org.mx.comps.jwt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    /**
     * Request参数在最后
     *
     * @param pjp     拦截点
     * @param around  环绕注解定义
     * @param request Request
     * @return 处理结果
     * @throws Throwable 处理过程中发生的异常
     */
    @Around(value = "@annotation(around) && args(request)")
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

    private Object around(ProceedingJoinPoint pjp, AuthenticateAround around, Request request) throws Throwable {
        Object result = authenticate(pjp);
        if (result == null) {
            // 身份认证成功，调取代理方法
            result = pjp.proceed();
        }
        return result;
    }

    private Object authenticate(ProceedingJoinPoint pjp) {
        // TODO 使用JWT进行身份认证
        if (logger.isDebugEnabled()) {
            logger.debug("Starting authenticate ....");
        }
        return null;
    }
}
