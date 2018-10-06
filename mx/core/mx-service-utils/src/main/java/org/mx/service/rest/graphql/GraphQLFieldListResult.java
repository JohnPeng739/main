package org.mx.service.rest.graphql;

import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public interface GraphQLFieldListResult<T> extends GraphQLField {
    List<T> executeForList(DataFetchingEnvironment environment);
}
