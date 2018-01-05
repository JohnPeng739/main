package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.service.ws.ConnectRuleFactory;
import org.mx.service.ws.ConnectionManager;
import org.mx.service.ws.rule.ConnectFilterRule;
import org.mx.spring.SpringContextHolder;

import java.io.InputStream;
import java.util.Set;

/**
 * Websocket实现基类
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class BaseWebsocket {
    private static final Log logger = LogFactory.getLog(BaseWebsocket.class);

    private String path = "/";

    private ConnectionManager manager1 = null;
    private boolean autoConfirmConnection = true;

    /**
     * 默认的构造函数
     */
    public BaseWebsocket() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param path                  访问路径
     * @param autoConfirmConnection 如果设置为true，则在收到消息后自动确认连接；否则需要应用手工确认连接。
     */
    public BaseWebsocket(String path, boolean autoConfirmConnection) {
        this();
        this.path = path;
        this.autoConfirmConnection = autoConfirmConnection;
    }

    /**
     * 获取Websocket服务的访问路径
     *
     * @return 路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 默认的连接成功回调方法
     *
     * @param session 会话
     */
    protected void afterConnect(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Allow the connection: %s:%d.",
                    TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress()),
                    session.getRemoteAddress().getPort()));
        }
    }

    /**
     * 默认的关闭前回调方法
     *
     * @param session    会话
     * @param statusCode 关闭号
     * @param reason     关闭原因
     */
    protected void beforeClose(Session session, int statusCode, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("ConnectionPoint closed, from: %s:%d, status: %d, reason: %s.",
                    TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress()),
                    session.getRemoteAddress().getPort(), statusCode, reason));
        }
    }

    /**
     * 默认的发生错误后的回调方法
     *
     * @param session   会话
     * @param throwable 错误异常
     */
    protected void afterError(Session session, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(String.format("Got error, from: %s:%d.",
                    TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress()),
                    session.getRemoteAddress().getPort()), throwable);
        }
    }

    /**
     * 默认的ping帧接收回调方法
     *
     * @param session 会话
     */
    protected void receivePing(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a ping frame, from: %s:%d.",
                    TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress()),
                    session.getRemoteAddress().getPort()));
        }
    }

    /**
     * 默认的消息帧接收回调方法
     *
     * @param session 会话
     * @param frame   消息帧
     */
    protected void receiveFrame(Session session, Frame frame) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a frame, from: %s:%d, frame: %s.",
                    TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress()),
                    session.getRemoteAddress().getPort(), frame.getClass().getName()));
        }
        if (frame instanceof PingFrame) {
            this.receivePing(session);
        }
    }

    /**
     * 默认的二进制消息接收回调方法
     *
     * @param session 会话
     * @param in      输入流
     */
    protected void receiveBinary(Session session, InputStream in) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a binary message, from: %s:%d.",
                    TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress()),
                    session.getRemoteAddress().getPort()));
        }
    }

    /**
     * 默认的文本消息接收回调方法
     *
     * @param session 会话
     * @param message 文本消息
     */
    protected void receiveText(Session session, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a text message, from: %s:%d,message: %s.",
                    TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress()),
                    session.getRemoteAddress().getPort(), message));
        }
    }

    /**
     * 确认连接有效
     *
     * @param session 连接会话
     */
    protected void confirmConnections(Session session) {
        ConnectionManager manager = SpringContextHolder.getBean(ConnectionManager.class);
        manager.confirmConnection(session);
    }

    /**
     * 判断指定的会话是否允许连接
     *
     * @param session 会话
     * @return 允许连接返回true，否则返回false。
     */
    private boolean allow(Session session) {
        ConnectRuleFactory factory = SpringContextHolder.getBean(ConnectRuleFactory.class);
        Set<ConnectFilterRule> connectFilterRules = factory.getRules();
        if (connectFilterRules == null || connectFilterRules.isEmpty()) {
            // 如果没有设置规则，默认允许所有连接
            return true;
        }
        for (ConnectFilterRule rule : connectFilterRules) {
            if (!rule.filter(session)) {
                // 只要有一个规则不允许连接，就拒绝连接
                return false;
            }
        }
        return true;
    }

    /**
     * 客户端连接服务器时被回调，进行黑白名单判定
     *
     * @param session 会话
     */
    @OnWebSocketConnect
    public final void onConnection(Session session) {
        ConnectionManager manager = SpringContextHolder.getBean(ConnectionManager.class);
        if (allow(session)) {
            manager.registryConnection(session);
            this.afterConnect(session);
        } else {
            manager.blockConnection(session);
        }
    }

    /**
     * 客户端发起关闭连接时被回调
     *
     * @param session    会话
     * @param statusCode 关闭码
     * @param reason     关闭原因
     */
    @OnWebSocketClose
    public final void onClose(Session session, int statusCode, String reason) {
        this.beforeClose(session, statusCode, reason);
        ConnectionManager manager = SpringContextHolder.getBean(ConnectionManager.class);
        manager.unregistryConnection(session);
    }

    /**
     * 发生错误时回调
     *
     * @param session   会话
     * @param throwable 错误异常
     */
    @OnWebSocketError
    public final void onError(Session session, Throwable throwable) {
        this.afterError(session, throwable);
    }

    /**
     * 收到一个消息帧时回调
     *
     * @param session 会话
     * @param frame   消息帧
     */
    @OnWebSocketFrame
    public final void onFrame(Session session, Frame frame) {
        this.receiveFrame(session, frame);
    }

    /**
     * 收到一个二进制消息时回调
     *
     * @param session 会话
     * @param in      输入流
     */
    @OnWebSocketMessage
    public final void onBinaryMessage(Session session, InputStream in) {
        if (autoConfirmConnection) {
            confirmConnections(session);
        }
        this.receiveBinary(session, in);
    }

    /**
     * 收到一个文本消息时回调
     *
     * @param session 会话
     * @param message 文本消息
     */
    @OnWebSocketMessage
    public final void onTextMessage(Session session, String message) {
        if (!StringUtils.isBlank(message) && autoConfirmConnection) {
            // 收到有效消息，且采用自动确认方式
            confirmConnections(session);
        }
        this.receiveText(session, message);
    }
}
