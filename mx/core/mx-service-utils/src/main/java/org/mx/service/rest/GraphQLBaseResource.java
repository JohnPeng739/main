package org.mx.service.rest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.graphql.GraphQLFactory;
import org.mx.service.rest.graphql.GraphQLRequest;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.mx.service.rest.vo.DataVO;

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

    /**
     * 执行一次GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param type      GraphQL操作类型
     * @param request   GraphQL操作请求对象
     * @return GraphQL执行结果
     */
    private DataVO<JSONObject> graphQL(String schemaKey, GraphQLType type, GraphQLRequest request) {
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
                logger.error(String.format("The GraphQL Schema[%s] not be defined.", request.getName()));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String requestString = String.format("%s%s%s", request.getName(),
                StringUtils.isBlank(request.getParam()) ? "" : "(" + request.getParam() + ")",
                StringUtils.isBlank(request.getResult()) ? "" : "{" + request.getResult() + "}");
        if (type == GraphQLType.MUTATION) {
            requestString = String.format("mutation{%s}", requestString);
        } else {
            requestString = String.format("query{%s}", requestString);
        }
        return new DataVO<>(graphQLUtils.execute(requestString));
    }

    /**
     * 执行一次数据查询类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param request   GraphGL请求对象
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> query(String schemaKey, GraphQLRequest request) {
        return graphQL(schemaKey, GraphQLType.QUERY, request);
    }

    /**
     * 执行一次数据操作类型的GraphQL
     *
     * @param schemaKey GraphQL Schema Key
     * @param request   GraphGL请求对象
     * @return GraphQL执行结果
     */
    protected DataVO<JSONObject> mutation(String schemaKey, GraphQLRequest request) {
        return graphQL(schemaKey, GraphQLType.MUTATION, request);
    }

    /**
     * GraphGL操作类型，包括：查询和数据操作
     */
    private enum GraphQLType {
        QUERY, MUTATION
    }
}
