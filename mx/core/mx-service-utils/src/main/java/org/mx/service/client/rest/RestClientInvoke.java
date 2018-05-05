package org.mx.service.client.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 基于Jetty的Restful服务的调用客户端工具类
 *
 * @author : john.peng created on date : 2017/11/4
 */
public class RestClientInvoke {
    private static final Log logger = LogFactory.getLog(RestClientInvoke.class);

    private Client client;

    public RestClientInvoke() {
        super();
        client = ClientBuilder.newClient();
    }

    public void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    private <R> R dowithResponse(Response response, Class<R> resultType) throws RestInvokeException {
        R r = null;
        String error = null;
        if (response.getStatus() == 200) {
            r = response.readEntity(resultType);
        } else {
            error = response.readEntity(String.class);
        }
        response.close();
        if (r != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoke RESTful service success.");
            }
            return r;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invoke RESTful service fail, response: %s.", error));
            }
            throw new RestInvokeException(error);
        }
    }

    public <R> R get(String url, Class<R> resultType) throws RestInvokeException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Request invoke GET RESTful service, url: %s, result type: %s.",
                    url, resultType.getName()));
        }
        WebTarget target = client.target(url);
        Response response = target.request().get();
        return dowithResponse(response, resultType);
    }

    public <R> R delete(String url, Class<R> resultType) throws RestInvokeException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Request invoke DELETE RESTful service, url: %s, result type: %s.",
                    url, resultType.getName()));
        }
        WebTarget target = client.target(url);
        Response response = target.request().delete();
        return dowithResponse(response, resultType);
    }

    public <R, D> R post(String url, D d, Class<R> resultType) throws RestInvokeException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Request invoke POST RESTful service, url: %s, result type: %s.",
                    url, resultType.getName()));
        }
        WebTarget target = client.target(url);
        Entity<D> entity = null;
        if (d != null) {
            entity = Entity.entity(d, MediaType.APPLICATION_JSON_TYPE);
        }
        Response response = target.request().post(entity);
        return dowithResponse(response, resultType);
    }

    public <R, D> R put(String url, D d, Class<R> resultType) throws RestInvokeException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Request invoke PUT RESTful service, url: %s, result type: %s.",
                    url, resultType.getName()));
        }
        WebTarget target = client.target(url);
        Entity<D> entity = null;
        if (d != null) {
            entity = Entity.entity(d, MediaType.APPLICATION_JSON_TYPE);
        }
        Response response = target.request().put(entity);
        return dowithResponse(response, resultType);
    }
}
