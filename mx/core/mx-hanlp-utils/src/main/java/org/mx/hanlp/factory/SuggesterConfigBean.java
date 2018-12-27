package org.mx.hanlp.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 推荐器配置对象
 *
 * @author john peng
 * Date time 2018/7/19 下午10:01
 */
public class SuggesterConfigBean {
    private static final Log logger = LogFactory.getLog(SuggesterConfigBean.class);

    @Value("${suggester.num:0}")
    private int num;

    private Environment env;

    public SuggesterConfigBean(Environment env) {
        super();
        this.env = env;
    }

    public int getNum() {
        return num;
    }

    public List<SuggesterProviderConfig> getSuggesterProviders() {
        List<SuggesterProviderConfig> providerConfigs = new ArrayList<>();
        for (int index = 1; index <= num; index++) {
            String type = env.getProperty(String.format("suggester.%d.type", index));
            String name = env.getProperty(String.format("suggester.%d.name", index));
            String provider = env.getProperty(String.format("suggester.%d.provider", index));
            if (StringUtils.isBlank(type) || StringUtils.isBlank(name) || StringUtils.isBlank(provider)) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Suggester's config invalid, type: %s, name: %s, provider: %s.",
                            type, name, provider));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            if ("csv".equalsIgnoreCase(type)) {
                providerConfigs.add(csvProvider(index, type, name, provider));
            } else if ("jdbc".equalsIgnoreCase(type)) {

                providerConfigs.add(jdbcProvider(index, type, name, provider));
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported type: %s.", type));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
                );
            }
        }
        return providerConfigs;
    }

    private SuggesterCsvProviderConfig csvProvider(int index, String type, String name, String provider) {
        String path = env.getProperty(String.format("suggester.%d.config.path", index));
        int id = env.getProperty(String.format("suggester.%d.config.fields.id", index), Integer.class, -1);
        int content = env.getProperty(String.format("suggester.%d.config.fields.content", index), Integer.class, -1);
        if (StringUtils.isBlank(path)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The file[%s] not found.", path));
            }
        }
        if (id < 0 || content < 0) {
            if (logger.isWarnEnabled()) {
                logger.error(String.format("Any param invalid, id: %d, content: %d.", id, content));
            }
        }
        return new SuggesterCsvProviderConfig(name, provider, path, id, content);
    }

    private SuggesterJdbcProviderConfig jdbcProvider(int index, String type, String name, String provider) {
        String url = env.getProperty(String.format("suggester.%d.config.url", index));
        String driver = env.getProperty(String.format("suggester.%d.config.driver", index));
        String user = env.getProperty(String.format("suggester.%d.config.user", index));
        String password = env.getProperty(String.format("suggester.%d.config.password", index));
        String id = env.getProperty(String.format("suggester.%d.config.id", index));
        String query = env.getProperty(String.format("suggester.%d.config.query", index));
        if (StringUtils.isBlank(url) || StringUtils.isBlank(driver) || StringUtils.isBlank(id) ||
                StringUtils.isBlank(query)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Any param invalid, url: %s, driver: %s, id: %s, query: %s.",
                        url, driver, id, query));
            }
        }
        return new SuggesterJdbcProviderConfig(name, provider, url, driver, user, password, id, query);
    }

    public class SuggesterProviderConfig {
        private String type, name, provider;

        public SuggesterProviderConfig(String type, String name, String provider) {
            super();
            this.type = type;
            this.name = name;
            this.provider = provider;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getProvider() {
            return provider;
        }
    }

    public class SuggesterCsvProviderConfig extends SuggesterProviderConfig {
        private String path;
        private int idField, contentField;

        public SuggesterCsvProviderConfig(String name, String provider, String path, int idField, int contentField) {
            super("csv", name, provider);
            this.path = path;
            this.idField = idField;
            this.contentField = contentField;
        }

        public String getPath() {
            return path;
        }

        public int getIdField() {
            return idField;
        }

        public int getContentField() {
            return contentField;
        }
    }

    public class SuggesterJdbcProviderConfig extends SuggesterProviderConfig {
        private String url, driver, user, password, idField, query;

        public SuggesterJdbcProviderConfig(String name, String provider, String url, String driver, String user, String password,
                                           String idField, String query) {
            super("jdbc", name, provider);
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.password = password;
            this.idField = idField;
            this.query = query;
        }

        public String getUrl() {
            return url;
        }

        public String getDriver() {
            return driver;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getIdField() {
            return idField;
        }

        public String getQuery() {
            return query;
        }
    }
}
