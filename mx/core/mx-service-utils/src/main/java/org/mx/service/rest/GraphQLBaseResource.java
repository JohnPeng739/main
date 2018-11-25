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

    public GraphQLBaseResource(GraphQLFactory factory) {
        super();
        this.factory = factory;
    }

    private DataVO<JSONObject> graphQL(String schemaKey, GraphQLType type, GraphQLRequest request) {
        if (request == null || StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getResult())) {
            if (logger.isErrorEnabled()) {
                logger.error("The graph request or name or result is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
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
                logger.error(String.format("The GraphQL Schema[%s] not be defined."));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String requestString = String.format("%s%s{%s}", request.getName(),
                StringUtils.isBlank(request.getParam()) ? "" : "(" + request.getParam() + ")",
                request.getResult());
        if (type == GraphQLType.MUTATION) {
            requestString = String.format("mutation{%s}", requestString);
        } else {
            requestString = String.format("query{%s}", requestString);
        }
        return new DataVO<>(graphQLUtils.execute(requestString));
    }

    protected DataVO<JSONObject> query(String schemaKey, GraphQLRequest request) {
        return graphQL(schemaKey, GraphQLType.QUERY, request);
    }

    protected DataVO<JSONObject> mutation(String schemaKey, GraphQLRequest request) {
        return graphQL(schemaKey, GraphQLType.MUTATION, request);
    }

    private enum GraphQLType {
        QUERY, MUTATION
    }
}
