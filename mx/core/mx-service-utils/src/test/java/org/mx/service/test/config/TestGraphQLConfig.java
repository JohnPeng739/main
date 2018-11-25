package org.mx.service.test.config;

import org.mx.service.rest.graphql.GraphQLConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@PropertySource({
        "classpath:graphql-config.properties"
})
public class TestGraphQLConfig {
    @Bean
    public GraphQLConfigBean graphQLConfigBean(Environment env) {
        return new GraphQLConfigBean(env);
    }
}
