package org.mx.tools.ffee.config;

import org.mx.dal.config.DalConfig;
import org.mx.service.rest.graphql.GraphQLTypeExecution;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.mx.service.server.config.ServerConfig;
import org.mx.spring.task.TaskFactory;
import org.mx.tools.ffee.rest.*;
import org.mx.tools.ffee.task.InitialBaseDataTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述： FFEE应用中Restful服务配置类。
 *
 * @author John.Peng
 * Date time 2018/2/18 上午11:57
 */
@Import({
        DalConfig.class,
        ServerConfig.class
})
@ComponentScan({
        "org.mx.tools.ffee.graphql",
        "org.mx.tools.ffee.restful",
        "org.mx.tools.ffee.task",
        "org.mx.tools.ffee.service.impl"
})
public class FfeeConfig {
    @Bean(name = "ffeeConfigBean")
    public FfeeConfigBean ffeeConfigBean() {
        return new FfeeConfigBean();
    }

    @Bean(name = "restfulClassesFFEE")
    public List<Class<?>> ffeeRestfulClasses() {
        return Arrays.asList(
                WxAuthorizationResource.class,
                AccountManageResource.class,
                FamilyManageResource.class,
                GraphQLResource.class
        );
    }

    @Bean("graphQLUtils")
    public GraphQLUtils ffeeGraphQLUtils(ApplicationContext context) {
        GraphQLUtils graphQLUtils = new GraphQLUtils();
        InputStream in = FfeeConfig.class.getResourceAsStream("/ffee.graphqls");
        List<GraphQLTypeExecution> typeExecutions = new ArrayList<>(
                context.getBeansOfType(GraphQLTypeExecution.class).values()
        );
        graphQLUtils.init(in, typeExecutions);
        return graphQLUtils;
    }

    @Bean
    public TaskFactory taskFactory(InitialBaseDataTask initialBaseDataTask) {
        TaskFactory taskFactory = new TaskFactory();
        taskFactory.addSingleAsyncTask(initialBaseDataTask);
        return taskFactory;
    }
}
