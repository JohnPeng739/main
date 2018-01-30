package org.mx.service.ws;

/**
 * Websocket连接生命周期监听接口定义
 *
 * @author : john.peng created on date : 2018/1/30
 */
public interface ConnectionLifeCycleListener {
    void beforeConnect(String connectKey);
    void afterConnect(String connectKey);
    void afterRegistry(String connectKey);
    void afterConfirm(String connectKey);
    void afterUnregistry(String connectKey);
    void beforeClose(String connectKey);
    void afterClose(String connectKey);
    void hasError(String connectKey, Throwable throwable);
}
