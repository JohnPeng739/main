package org.mx.service.rest.graphql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import graphql.ExceptionWhileDataFetching;
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
import org.mx.error.UserInterfaceException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.error.UserInterfaceServiceErrorException;
import org.springframework.context.ApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    /**
     * 根据指定的GraphQL模式文件初始化GraphQL
     *
     * @param name          GraphQL模式文件名
     * @param executionList 类型执行器列表
     */
    public void initByClasspath(String name, List<GraphQLTypeExecution> executionList) {
        init(GraphQLUtils.class.getResourceAsStream(name), executionList);
    }

    /**
     * 根据指定的GraphQL模式文件（包路径方式）初始化GraphQL
     *
     * @param name          GraphQL模式文件名
     * @param executionList 类型执行器列表
     */
    public void initByFile(String name, List<GraphQLTypeExecution> executionList) {
        try (FileInputStream fis = new FileInputStream(name)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Initialize the graphql withe execution list.");
            }
            init(fis, executionList);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The file[%s] I/O fail.", name), ex);
            }
            throw new UserInterfaceServiceErrorException(
                    UserInterfaceServiceErrorException.ServiceErrors.GRAPHQL_FILE_READ_FAIL
            );
        }
    }

    /**
     * 根据指定的GraphQL模式文件输入流初始化GraphQL
     *
     * @param in             GraphQL模式文件输入流
     * @param executionsList 类型执行器列表
     */
    public void init(InputStream in, List<GraphQLTypeExecution> executionsList) {
        // 加载SDL的Schema文件
        if (executionsList == null || executionsList.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("The execution list is empty.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        init(in, builder -> {
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
            return null;
        });
    }

    /**
     * 根据指定的GraphQL模式文件初始化GraphQL
     *
     * @param name    GraphQL模式文件名
     * @param context Spring IoC容器上下文
     */
    public void initByClasspath(String name, ApplicationContext context) {
        init(GraphQLUtils.class.getResourceAsStream(name), context);
    }

    /**
     * 根据指定的GraphQL模式文件（包路径方式）初始化GraphQL
     *
     * @param name    GraphQL模式文件名
     * @param context Spring IoC容器上下文
     */
    public void initByFile(String name, ApplicationContext context) {
        try (FileInputStream fis = new FileInputStream(name)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Initialize the graphql withe application context.");
            }
            init(fis, context);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The file[%s] I/O fail.", name), ex);
            }
            throw new UserInterfaceServiceErrorException(
                    UserInterfaceServiceErrorException.ServiceErrors.GRAPHQL_FILE_READ_FAIL
            );
        }
    }

    /**
     * 根据指定的GraphQL模式文件输入流初始化GraphQL
     *
     * @param in      GraphQL模式文件输入流
     * @param context Spring IoC容器上下文
     */
    public void init(InputStream in, ApplicationContext context) {
        if (context == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The application context is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Map<String, GraphQLAnnotationExecution> executionMap = context.getBeansOfType(GraphQLAnnotationExecution.class);
        if (executionMap.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There may not has any graphql annotation type.");
            }
            return;
        }
        init(in, builder -> {
            for (GraphQLAnnotationExecution execution : executionMap.values()) {
                Method[] methods = execution.getClass().getMethods();
                for (Method method : methods) {
                    GraphQLFieldAnnotation annotation = method.getAnnotation(GraphQLFieldAnnotation.class);
                    if (annotation != null) {
                        String name = annotation.value();
                        if (StringUtils.isBlank(name)) {
                            if (logger.isErrorEnabled()) {
                                logger.error("The annotation's field name is blank.");
                            }
                            throw new UserInterfaceSystemErrorException(
                                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                            );
                        }
                        builder.type(execution.getTypeName(), typeWiring -> {
                            typeWiring.dataFetcher(name, environment -> {
                                try {
                                    return method.invoke(execution, environment);
                                } catch (IllegalAccessException | InvocationTargetException ex) {
                                    if (ex.getCause() instanceof UserInterfaceException) {
                                        throw (UserInterfaceException) ex.getCause();
                                    } else {
                                        if (logger.isErrorEnabled()) {
                                            logger.error(String.format("Invoke method[%s] fail.", method.getName()), ex);
                                        }
                                        throw new UserInterfaceServiceErrorException(
                                                UserInterfaceServiceErrorException.ServiceErrors.GRAPHQL_EXECUTE_FAIL
                                        );
                                    }
                                }
                            });
                            return typeWiring;
                        });
                    }
                }
            }
            return null;
        });
    }

    /**
     * 初始化GraphQL
     *
     * @param in   GrahpQL模式文件输入流
     * @param func 自定义的初始化执行函数
     */
    private void init(InputStream in, Function<RuntimeWiring.Builder, Void> func) {
        if (in == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The graphql schema input stream is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        SchemaParser parser = new SchemaParser();
        TypeDefinitionRegistry registry = parser.parse(new InputStreamReader(in));

        // 根据自定义实现装配RuntimeWiring
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
        func.apply(builder);
        RuntimeWiring wiring = builder.build();
        // 初始化GraphQL
        SchemaGenerator generator = new SchemaGenerator();
        GraphQLSchema schema = generator.makeExecutableSchema(registry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    /**
     * 执行一个GraphQL请求
     *
     * @param request GraphQL请求字符串
     * @return 执行结果，采用JSONObject结构
     */
    public JSONObject execute(String request) {
        ExecutionResult result = graphQL.execute(request);
        List<GraphQLError> errors = result.getErrors();
        if (errors != null && !errors.isEmpty()) {
            // error
            if (errors.get(0) instanceof ExceptionWhileDataFetching &&
                    ((ExceptionWhileDataFetching) errors.get(0)).getException() instanceof UserInterfaceException) {
                throw (UserInterfaceException) ((ExceptionWhileDataFetching) errors.get(0)).getException();
            }
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
