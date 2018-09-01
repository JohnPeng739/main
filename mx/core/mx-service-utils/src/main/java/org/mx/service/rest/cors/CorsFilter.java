package org.mx.service.rest.cors;

import org.mx.spring.utils.SpringContextHolder;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * 描述： RESTful处理跨域的过滤器，适用于Jersey。
 *
 * @author john peng
 * Date time 2018/9/1 上午11:09
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {
    private CorsConfigBean corsConfigBean;

    public CorsFilter() {
        super();
        corsConfigBean = SpringContextHolder.getBean(CorsConfigBean.class);
    }

    /**
     * {@inheritDoc}
     *
     * @see ContainerResponseFilter#filter(ContainerRequestContext, ContainerResponseContext)
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", corsConfigBean.getAllowOrigin());
        headers.add("Access-Control-Allow-Headers", corsConfigBean.getAllowHeaders());
        headers.add("Access-Control-Expose-Headers", corsConfigBean.getExposeHeader());
        headers.add("Access-Control-Allow-Credentials", corsConfigBean.getAllowCredentials());
        headers.add("Access-Control-Allow-Methods", corsConfigBean.getAllowMethods());
        headers.add("Access-Control-Max-Age", corsConfigBean.getMaxAge());
    }
}
