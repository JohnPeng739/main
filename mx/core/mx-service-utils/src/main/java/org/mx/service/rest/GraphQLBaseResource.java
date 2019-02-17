package org.mx.service.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.graphql.GraphQLFactory;
import org.mx.service.rest.graphql.GraphQLRequest;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.mx.service.rest.vo.DataVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述： GraphQL类型的RESTful基础服务定义
 *
 * @author john peng
 * Date time 2018/11/25 12:03 PM
 */
public abstract class GraphQLBaseResource {
    private static final Log logger = LogFactory.getLog(GraphQLBaseResource.class);

    protected GraphQLFactory factory;

    /**
     * 构造函数
     *
     * @param factory GraphQL工厂接口
     */
    public GraphQLBaseResource(GraphQLFactory factory) {
        super();
        this.factory = factory;
    }

    private List<String> createGraphQLString(List<GraphQLRequest> requests) {
        List<String> list = new ArrayList<>();
        for (GraphQLRequest request : requests) {
            if (request == null || StringUtils.isBlank(request.getName())) {
                if (logger.isErrorEnabled()) {
                    logger.error("The graph request or name is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            // 如果没有设置result字段，表示查询的结果为原始结果
            if (StringUtils.isBlank(request.getResult())) {
                request.setResult("");
            }
            String requestString = String.format("%s%s%s", request.getName(),
                    StringUtils.isBlank(request.getParam()) ? "" : "(" + request.getParam() + ")",
                    StringUtils.isBlank(request.getResult()) ? "" : "{" + request.getResult() + "}");
            list.add(requestString);
        }
        return list;
    }

    /**
     * 执行一次GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param type      GraphQL操作类型
     * @param requests   GraphQL操作请求对象列表
     * @return GraphQL执行结果
     */
    private DataVO<JSONObject> graphQL(String schemaKey, GraphQLType type, List<GraphQLRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("The graph request is null or name is blank or requests is empty.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String requestString = StringUtils.merge(createGraphQLString(requests), " ");
        if (factory == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The GrahpQL factory is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        GraphQLUtils graphQLUtils = factory.getUtils(schemaKey);
        if (graphQLUtils == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The GraphQL Schema[%s] not be defined.", schemaKey));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (type == GraphQLType.MUTATION) {
            requestString = String.format("mutation{%s}", requestString);
        } else {
            requestString = String.format("query{%s}", requestString);
        }
        return new DataVO<>(graphQLUtils.execute(requestString));
    }

    private GraphQLRequest prepareSingleRequest(JSONObject json) {
        if (json == null) {
            return null;
        }
        String name = json.getString("name"),
                param = json.getString("param"),
                result = json.getString("result");
        return new GraphQLRequest(name, param, result);
    }

    private List<GraphQLRequest> prepareRequestFromJson(JSON request) {
        if (request == null) {
            return null;
        }
        if (request instanceof JSONArray) {
            JSONArray array = (JSONArray)request;
            List<GraphQLRequest> requests = new ArrayList<>();
            for (int index = 0; index < array.size(); index ++) {
                requests.add(prepareSingleRequest(array.getJSONObject(index)));
            }
            return requests;
        } else {
            JSONObject json = (JSONObject)request;
            return Collections.singletonList(prepareSingleRequest(json));
        }
    }

    /**
     * 执行一次数据查询类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param request   GraphGL请求对象
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> query(String schemaKey, GraphQLRequest request) {
        return graphQL(schemaKey, GraphQLType.QUERY, Collections.singletonList(request));
    }

    /**
     * 执行一次数据查询类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param requests   GraphGL请求对象列表
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> query(String schemaKey, List<GraphQLRequest> requests) {
        return graphQL(schemaKey, GraphQLType.QUERY, requests);
    }

    /**
     * 执行一次数据查询类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param request   GraphGL请求对象列表(JSON)
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> query(String schemaKey, JSON request) {
        return graphQL(schemaKey, GraphQLType.QUERY, prepareRequestFromJson(request));
    }

    /**
     * 执行一次数据操作类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param request   GraphGL请求对象
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> mutation(String schemaKey, GraphQLRequest request) {
        return graphQL(schemaKey, GraphQLType.MUTATION, Collections.singletonList(request));
    }

    /**
     * 执行一次数据操作类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param requests   GraphGL请求对象列表
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> mutation(String schemaKey, List<GraphQLRequest> requests) {
        return graphQL(schemaKey, GraphQLType.MUTATION, requests);
    }

    /**
     * 执行一次数据操作类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param request   GraphGL请求对象(JSON)
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> mutation(String schemaKey, JSON request) {
        return graphQL(schemaKey, GraphQLType.MUTATION, prepareRequestFromJson(request));
    }

    /**
     * GraphGL操作类型，包括：查询和数据操作
     */
    private enum GraphQLType {
        QUERY, MUTATION
    }
}
