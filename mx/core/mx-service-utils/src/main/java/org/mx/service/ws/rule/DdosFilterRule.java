package org.mx.service.ws.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.service.ws.ConnectionManager;
import org.mx.spring.SpringContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 防止阻塞攻击的过滤规则
 *
 * @author : john.peng created on date : 2018/1/4
 */
public class DdosFilterRule implements ConnectFilterRule {
    private static final Log logger = LogFactory.getLog(DdosFilterRule.class);

    private ConnectionManager manager = null;

    private int testCycleSec = 10, maxNumber = 30, maxIdleSec = 30;

    /**
     * {@inheritDoc}
     *
     * @see ConnectFilterRule#getName()
     */
    @Override
    public String getName() {
        return "Ddos filter rule";
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectFilterRule#init(String)
     */
    @Override
    public void init(String key) {
        if (StringUtils.isBlank(key)) {
            if (logger.isErrorEnabled()) {
                logger.error("The ddos filter rule' configuration error");
            }
            return;
        }
        Environment env = SpringContextHolder.getApplicationContext().getEnvironment();
        manager = SpringContextHolder.getBean(ConnectionManager.class);
        testCycleSec = env.getProperty(String.format("%s.testCycleSec", key), Integer.class, 10);
        maxNumber = env.getProperty(String.format("%s.maxNumber", key), Integer.class, 30);
        maxIdleSec = env.getProperty(String.format("%s.maxIdleSec", key), Integer.class, 30);
        manager.setDdosParameters(testCycleSec, maxNumber, maxIdleSec);
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectFilterRule#filter(Session)
     */
    @Override
    public boolean filter(Session session) {
        return !manager.isDdos(session);
    }
}
