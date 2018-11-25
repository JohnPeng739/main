package org.mx.service.rest.graphql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述： 根据GraphQL Schema创建GraphQL的工程类
 *
 * @author john peng
 * Date time 2018/11/25 11:12 AM
 */
public class GraphQLFactory {
    private static final Log logger = LogFactory.getLog(GraphQLFactory.class);

    private Map<String, GraphQLUtils> graphQLUtilsMap = new HashMap<>();

    private ApplicationContext context;

    public GraphQLFactory(ApplicationContext context) {
        super();
        this.context = context;
    }

    /**
     * 根据指定的GraphQL Schema Key获取GraphQL工具
     *
     * @param key Key
     * @return GraphQL工具
     */
    public GraphQLUtils getUtils(String key) {
        return graphQLUtilsMap.get(key);
    }

    /**
     * 根据配置初始化GraphQL工厂
     */
    public void init() {
        GraphQLConfigBean config = context.getBean(GraphQLConfigBean.class);
        if (config.getConfig().size() <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("There are not any GraphQL Schema be defined.");
            }
            return;
        }
        List<GraphQLConfigBean.GraphQLItem> items = config.getConfig();
        items.forEach(item -> {
            GraphQLUtils utils = new GraphQLUtils();
            if (item.getPathType() == GraphQLConfigBean.PathType.CLASS_PATH) {
                utils.initByClasspath(item.getSchemaPath(), context);
            } else {
                utils.initByFile(item.getSchemaPath(), context);
            }
            graphQLUtilsMap.put(item.getKey(), utils);
        });
    }

    /**
     * 销毁GraphQL工厂
     */
    public void destroy() {
        graphQLUtilsMap.clear();
    }
}
