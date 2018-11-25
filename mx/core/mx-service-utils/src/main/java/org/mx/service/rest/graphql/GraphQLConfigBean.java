package org.mx.service.rest.graphql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： GraphQL配置信息定义类
 *
 * @author john peng
 * Date time 2018/11/25 11:21 AM
 */
public class GraphQLConfigBean {
    private static final Log logger = LogFactory.getLog(GraphQLConfigBean.class);

    @Value("${graphql.num:0}")
    private int num;

    private Environment env;

    /**
     * 构造函数
     *
     * @param env Spring IoC上下文环境
     */
    public GraphQLConfigBean(Environment env) {
        super();
        this.env = env;
    }

    /**
     * 获取GraphQL的相关配置信息
     *
     * @return GraphQL配置信息
     */
    public List<GraphQLItem> getConfig() {
        List<GraphQLItem> config = new ArrayList<>();
        for (int index = 1; index <= num; index++) {
            String key = env.getProperty(String.format("graphql.%d.key", index)),
                    path = env.getProperty(String.format("graphql.%d.schema", index)),
                    type = env.getProperty(String.format("graphql.%d.pathType", index));
            if (!StringUtils.isBlank(key) && !StringUtils.isBlank(path)) {
                if (StringUtils.isBlank(type)) {
                    config.add(new GraphQLItem(key, path, PathType.CLASS_PATH));
                } else {
                    config.add(new GraphQLItem(key, path, PathType.valueOf(type)));
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("The graphQL's key or schema path is blank.");
                }
                break;
            }
        }
        return config;
    }

    public enum PathType {
        CLASS_PATH, FILE_PATH
    }

    /**
     * GraphQL配置项
     */
    public class GraphQLItem {
        private String key;
        private String schemaPath;
        private PathType pathType = PathType.CLASS_PATH;

        /**
         * 构造函数
         *
         * @param key  Key
         * @param path GraphQL Schema文件路径
         * @param type GraphQL Schema文件路径类型
         */
        private GraphQLItem(String key, String path, PathType type) {
            super();
            this.key = key;
            this.schemaPath = path;
            this.pathType = type;
        }

        /**
         * 获取GraphQL Key
         *
         * @return Key
         */
        public String getKey() {
            return key;
        }

        /**
         * 获取GraphQL Schema文件路径
         *
         * @return 文件路径
         */
        public String getSchemaPath() {
            return schemaPath;
        }

        /**
         * 获取GraphQL Schema文件路径类型
         *
         * @return 文件路径类型
         */
        public PathType getPathType() {
            return pathType;
        }
    }
}
