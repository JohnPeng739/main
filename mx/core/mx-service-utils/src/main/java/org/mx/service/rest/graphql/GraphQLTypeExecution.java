package org.mx.service.rest.graphql;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 用户自定义的GraphQL执行器组类定义
 *
 * @author john peng
 * Date time 2018/10/4 下午2:03
 */
public class GraphQLTypeExecution {
    private String typeName = null;
    private List<GraphQLField> executions = null;

    private GraphQLTypeExecution() {
        super();
        this.executions = new ArrayList<>();
    }

    public GraphQLTypeExecution(String typeName) {
        this();
        this.typeName = typeName;
    }

    public GraphQLTypeExecution addExecution(GraphQLField execution) {
        executions.add(execution);
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<GraphQLField> getExecutions() {
        return executions;
    }
}
