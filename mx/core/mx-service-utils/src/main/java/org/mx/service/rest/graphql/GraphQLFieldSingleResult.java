package org.mx.service.rest.graphql;

import graphql.schema.DataFetchingEnvironment;

public interface GraphQLFieldSingleResult<T> extends GraphQLField {
    T executeForSingle(DataFetchingEnvironment environment);
}
