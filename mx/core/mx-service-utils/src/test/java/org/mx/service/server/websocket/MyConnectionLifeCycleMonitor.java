package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.service.ws.BaseConnectionLifeCycleMonitor;
import org.springframework.stereotype.Component;

@Component
public class MyConnectionLifeCycleMonitor extends BaseConnectionLifeCycleMonitor {
    private static final Log logger = LogFactory.getLog(MyConnectionLifeCycleMonitor.class);

    @Override
    public void beforeConnect(String connectKey) {
        if (logger.isInfoEnabled()) {
            logger.info("-----------------------------------------------------------------");
        }
        super.beforeConnect(connectKey);
    }

    @Override
    public void afterClose(String connectKey) {
        super.afterClose(connectKey);
        if (logger.isInfoEnabled()) {
            logger.info("*****************************************************************");
        }
    }
}
