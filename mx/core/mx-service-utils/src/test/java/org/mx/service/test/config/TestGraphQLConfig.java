package org.mx.service.test.config;

import org.mx.service.rest.graphql.GraphQLConfigBean;
import org.mx.service.server.config.ServerConfig;
import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@PropertySource({
        "classpath:graphql.properties"
})
@Import({
        SpringConfig.class,
        ServerConfig.class
})
@ComponentScan({
        "org.mx.service.test.graphql"
})
public class TestGraphQLConfig {
    @Bean
    public GraphQLConfigBean graphQLConfigBean(Environment env) {
        return new GraphQLConfigBean(env);
    }
}
