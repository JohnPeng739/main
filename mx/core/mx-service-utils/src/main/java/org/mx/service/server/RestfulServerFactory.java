package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Handler;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.jetty.JettyHttpContainerProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.mx.StringUtils;
import org.mx.service.rest.ServerStatisticResource;
import org.mx.service.rest.UserInterfaceExceptionMapper;
import org.mx.service.rest.auth.RestAuthenticateFilter;
import org.mx.service.rest.cors.CorsConfigBean;
import org.mx.service.rest.cors.CorsFilter;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 描述： 基于Jetty的Http服务的工程类
 *
 * @author John.Peng
 * Date time 2018/3/11 上午11:11
 */
public class RestfulServerFactory extends HttpServerFactory {
    private static final Log logger = LogFactory.getLog(RestfulServerFactory.class);

    private ApplicationContext context;
    private RestfulServerConfigBean restfulServerConfigBean;
    private CorsConfigBean corsConfigBean;

    /**
     * 默认的构造函数
     *
     * @param context                 Spring IoC上下文
     * @param restfulServerConfigBean RESTful配置对象
     * @param corsConfigBean          跨域配置对象
     */
    public RestfulServerFactory(ApplicationContext context, RestfulServerConfigBean restfulServerConfigBean,
                                CorsConfigBean corsConfigBean) {
        super(restfulServerConfigBean);
        this.context = context;
        this.restfulServerConfigBean = restfulServerConfigBean;
        this.corsConfigBean = corsConfigBean;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerFactory#getHandler()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Handler getHandler() {
        String[] serverClasses = restfulServerConfigBean.getServiceClasses();
        if (serverClasses == null || serverClasses.length <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("You not define any restful service resource, will not create the HttpServer.");
            }
            return null;
        } else {
            ResourceConfig config = new ResourceConfig();
            config.register(UriConnegFilter.class);
            config.register(JettyHttpContainerProvider.class);
            config.register(RequestContextFilter.class);
            for (String classesDef : serverClasses) {
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
            // 根据需要启用服务器统计数据
            if (restfulServerConfigBean.isStatEnabled()) {
                config.register(ServerStatisticResource.class);
            }
            // 根据需要注册跨域过滤器
            if (corsConfigBean.isEnabled()) {
                config.register(CorsFilter.class);
            }
            // 注册身份令牌过滤器
            config.register(RestAuthenticateFilter.class);
            // 注册通用的UserInterface异常处理类
            config.register(UserInterfaceExceptionMapper.class);
            // 注册Multipart
            config.register(MultiPartFeature.class);
            /*
             * 为了使用Spring IoC注入，需要将ApplicationContext事先注入
             */
            config.property("contextConfig", context);
            return ContainerFactory.createContainer(JettyHttpContainer.class, config);
        }
    }
}
