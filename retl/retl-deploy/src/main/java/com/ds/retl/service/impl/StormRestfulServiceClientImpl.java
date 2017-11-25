package com.ds.retl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.StormRestfulServiceClient;
import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 访问Storm提供的Restful服务的实现类
 *
 * @author : john.peng created on date : 2017/10/30
 */
@Component
public class StormRestfulServiceClientImpl implements StormRestfulServiceClient {
    private Client client = null;

    @Autowired
    private Environment env = null;

    /**
     * 默认的构造函数
     */
    public StormRestfulServiceClientImpl() {
        super();
        this.client = ClientBuilder.newClient();
    }

    /**
     * 拼装Storm Restful服务地址
     *
     * @param url 业务目标地址
     * @return 完整的服务地址
     * @throws UserInterfaceErrorException 拼装过程中发生的异常
     */
    private String prepareUri(String url) throws UserInterfaceErrorException {
        if (StringUtils.isBlank(url)) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.STORM_URL_BLANK);
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        String baseUri = env.getProperty("storm.api");
        if (StringUtils.isBlank(baseUri)) {
            baseUri = "http://localhost:8080/api/v1";
        }
        return String.format("%s%s", baseUri, url);
    }

    /**
     * 使用Jersey发起一次GET操作
     *
     * @param url 业务地址
     * @return GET返回的数据，JSON对象
     * @throws UserInterfaceErrorException 调用过程中发生的异常
     */
    private JSONObject invokeGet(String url) throws UserInterfaceErrorException {
        String uri = prepareUri(url);
        WebTarget target = this.client.target(uri);
        Response response = target.request().get();
        String jsonString = response.readEntity(String.class);
        response.close();
        JSONObject json = JSON.parseObject(jsonString);
        if (response.getStatus() == 500) {
            throw new UserInterfaceErrorException(json.getString("error"));
        } else {
            return json;
        }
    }

    /**
     * 使用Jersey发起一次POST操作
     *
     * @param url       业务地址
     * @param content   POST的内容
     * @param mediaType POST内容的类型
     * @param <T>       POST内容的泛型定义
     * @return POST返回的数据，JSON对象
     * @throws UserInterfaceErrorException 调用过程中发生的异常
     */
    private <T> JSONObject invokePost(String url, T content, MediaType mediaType) throws UserInterfaceErrorException {
        String uri = prepareUri(url);
        WebTarget target = this.client.target(uri);
        Entity entity = null;
        if (content != null) {
            entity = Entity.entity(content, mediaType);
        }
        Response response = target.request().post(entity);
        String jsonString = response.readEntity(String.class);
        response.close();
        JSONObject json = JSON.parseObject(jsonString);
        if (response.getStatus() == 500) {
            throw new UserInterfaceErrorException(json.getString("error"));
        } else {
            return json;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#getClusterSummary()
     */
    @Override
    public JSONObject getClusterSummary() throws UserInterfaceErrorException {
        return invokeGet("/cluster/summary");
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#getClusterNimbusSummary()
     */
    @Override
    public JSONArray getClusterNimbusSummary() throws UserInterfaceErrorException {
        JSONObject json = invokeGet("/nimbus/summary");
        if (json != null && json.containsKey("nimbuses")) {
            return json.getJSONArray("nimbuses");
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#getClusterSupervisorSummary()
     */
    @Override
    public JSONArray getClusterSupervisorSummary() throws UserInterfaceErrorException {
        JSONObject json = invokeGet("/supervisor/summary");
        if (json != null && json.containsKey("supervisors")) {
            return json.getJSONArray("supervisors");
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#getToptologies()
     */
    @Override
    public JSONArray getToptologies() throws UserInterfaceErrorException {
        JSONObject json = invokeGet("/topology/summary");
        if (json != null && json.containsKey("topologies")) {
            return json.getJSONArray("topologies");
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#getTopology(String)
     */
    @Override
    public JSONObject getTopology(String id) {
        try {
            return invokeGet(String.format("/topology/%s", id));
        } catch (UserInterfaceErrorException ex) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#rebalance(String)
     */
    @Override
    public JSONObject rebalance(String topologyId) throws UserInterfaceErrorException {
        return rebalance(topologyId, 6);
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#rebalance(String, int)
     */
    @Override
    public JSONObject rebalance(String topologyId, int waitTime) throws UserInterfaceErrorException {
        if (waitTime < 6) {
            waitTime = 6;
        }
        return invokePost(String.format("/topology/%s/rebalance/%d", topologyId, waitTime),
                null, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#kill(String)
     */
    @Override
    public JSONObject kill(String topologyId) throws UserInterfaceErrorException {
        return kill(topologyId, 6);
    }

    /**
     * {@inheritDoc}
     *
     * @see StormRestfulServiceClient#kill(String, int)
     */
    @Override
    public JSONObject kill(String topologyId, int waitTime) throws UserInterfaceErrorException {
        if (waitTime < 6) {
            waitTime = 6;
        }
        return invokePost(String.format("/topology/%s/kill/%d", topologyId, waitTime),
                null, MediaType.APPLICATION_JSON_TYPE);
    }
}
