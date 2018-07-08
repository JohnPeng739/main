package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Handler;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.jetty.JettyHttpContainerProvider;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.mx.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 描述： 基于Jetty的Http服务的工程类
 *
 * @author John.Peng
 * Date time 2018/3/11 上午11:11
 */
public class RestfulServerFactory extends HttpServerFactory {
    private static final Log logger = LogFactory.getLog(RestfulServerFactory.class);

    private Environment env;
    private ApplicationContext context;

    /**
     * 默认的构造函数
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     */
    public RestfulServerFactory(Environment env, ApplicationContext context) {
        super(env, "restful");
        this.env = env;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerFactory#getHandler()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Handler getHandler() {
        String serviceClassesDef = "restful.service.classes";
        String restfulServiceClasses = env.getProperty(serviceClassesDef);
        if (StringUtils.isBlank(restfulServiceClasses)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("You not define [%s], will not create the HttpServer.", serviceClassesDef));
            }
            return null;
        } else {
            ResourceConfig config = new ResourceConfig();
            config.register(UriConnegFilter.class);
            config.register(JettyHttpContainerProvider.class);
            config.register(RequestContextFilter.class);
            String[] classesDefs = restfulServiceClasses.split(",");
            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> restfulClasses = (List) context.getBean(classesDef, List.class);
                    if (!restfulClasses.isEmpty()) {
                        /*
                         * 如果引入对象实例，将导致Provider接口警告，修改为注册类；
                         * 然后将ApplicationContext手工注入
                         */
                        restfulClasses.forEach(config::register);
                    }
                }
            }
            /*
             * 为了使用Spring IoC注入，需要将ApplicationContext事先注入
             */
            config.property("contextConfig", context);
            return ContainerFactory.createContainer(JettyHttpContainer.class, config);
        }
    }
}
