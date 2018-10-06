package org.mx.service.rest.graphql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.error.UserInterfaceServiceErrorException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * 描述： 使用GraphQL进行快速RESTful重构的工具类
 *
 * @author john peng
 * Date time 2018/10/4 下午1:55
 */
public class GraphQLUtils {
    private static final Log logger = LogFactory.getLog(GraphQLUtils.class);

    private GraphQL graphQL = null;

    public static <T> T parse(Map<String, Object> input, Class<T> clazz) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return JSON.parseObject(new JSONObject(input).toJSONString(), clazz);
    }

    public void init(InputStream in, List<GraphQLTypeExecution> executionsList) {
        // 加载SDL的Schema文件
        if (in == null || executionsList == null || executionsList.isEmpty()) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        SchemaParser parser = new SchemaParser();
        TypeDefinitionRegistry registry = parser.parse(new InputStreamReader(in));

        // 根据自定义实现装配RuntimeWiring
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
        for (GraphQLTypeExecution typeExecution : executionsList) {
            if (typeExecution == null || StringUtils.isBlank(typeExecution.getTypeName())) {
                if (logger.isErrorEnabled()) {
                    logger.error("The GraphQL executions is null or it's type name is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            String typeName = typeExecution.getTypeName();
            List<GraphQLField> fieldExecutions = typeExecution.getExecutions();
            if (fieldExecutions == null || fieldExecutions.isEmpty()) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The type[%s]'s execution is null or empty.", typeName));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            builder.type(typeName, typeWiring -> {
                fieldExecutions.forEach(fieldExecution -> {
                    if (fieldExecution == null || StringUtils.isBlank(fieldExecution.getFieldName())) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(String.format(
                                    "The type[%s]'s field execution is null or the field name is blank.", typeName));
                        }
                    } else {

                        if (fieldExecution instanceof GraphQLFieldSingleResult) {
                            // 根据字段名创建一个获取单条记录的执行器
                            typeWiring.dataFetcher(
                                    fieldExecution.getFieldName(),
                                    ((GraphQLFieldSingleResult) fieldExecution)::executeForSingle
                            );
                        }
                        if (fieldExecution instanceof GraphQLFieldListResult) {
                            // 根据字段名+List创建一个获取多条记录的执行器
                            typeWiring.dataFetcher(
                                    String.format("%sList", fieldExecution.getFieldName()),
                                    ((GraphQLFieldListResult) fieldExecution)::executeForList
                            );
                        }
                    }
                });
                return typeWiring;
            });
        }
        RuntimeWiring wiring = builder.build();

        // 初始化GraphQL
        SchemaGenerator generator = new SchemaGenerator();
        GraphQLSchema schema = generator.makeExecutableSchema(registry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    public JSONObject execute(String request) {
        ExecutionResult result = graphQL.execute(request);
        List<GraphQLError> errors = result.getErrors();
        if (errors != null && !errors.isEmpty()) {
            // error
            if (logger.isErrorEnabled()) {
                StringBuffer sb = new StringBuffer("errors: ");
                errors.forEach(error -> sb.append(String.format("[(%s): %s.]",
                        error.getErrorType().name(), error.getMessage())));
                logger.error(sb.toString());
            }
            throw new UserInterfaceServiceErrorException(
                    UserInterfaceServiceErrorException.ServiceErrors.GRAPHQL_EXECUTE_FAIL
            );
        } else {
            // success
            return new JSONObject(result.getData());
        }
    }
}
