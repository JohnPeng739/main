package org.mx.service.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 默认的连接生命周期监视器，最基础的实现
 *
 * @author : john.peng created on date : 2018/1/30
 */
public class BaseConnectionLifeCycleMonitor implements ConnectionLifeCycleListener {
    private static final Log logger = LogFactory.getLog(BaseConnectionLifeCycleMonitor.class);

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#beforeConnect(String)
     */
    @Override
    public void beforeConnect(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Before connect, connect key: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#afterConnect(String)
     */
    @Override
    public void afterConnect(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After connect, connect key: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#afterRegistry(String)
     */
    @Override
    public void afterRegistry(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After registry, connect key: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#afterConfirm(String)
     */
    @Override
    public void afterConfirm(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After confirm connect, connect key: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#afterUnregistry(String)
     */
    @Override
    public void afterUnregistry(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After unregistry, connect key: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#beforeClose(String)
     */
    @Override
    public void beforeClose(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Before close, connect key: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#afterClose(String)
     */
    @Override
    public void afterClose(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After close, connect key: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ConnectionLifeCycleListener#hasError(String, Throwable)
     */
    @Override
    public void hasError(String connectKey, Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Some error, connect key: %s.", connectKey), throwable);
        }
    }
}
