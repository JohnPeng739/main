package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.mx.StringUtils;
import org.mx.TypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Websocket实现基类
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class BaseWebsocket {
    private static final Log logger = LogFactory.getLog(BaseWebsocket.class);

    private String path = "/";
    private Set<List<Byte>> allows, blocks;

    /**
     * 默认的构造函数
     */
    public BaseWebsocket() {
        super();
        this.allows = new HashSet<>();
        this.blocks = new HashSet<>();
    }

    /**
     * 默认的构造函数
     *
     * @param path 访问路径
     */
    public BaseWebsocket(String path) {
        this();
        this.path = path;
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
     * 添加指定的过滤规则，按照IP字节顺序过滤
     * @param filter 过滤规则
     * @param set 存放过滤集合
     */
    private void addFilter(String filter, Set<List<Byte>> set) {
        String[] ips = StringUtils.split(filter, ",", true, true);
        if (ips == null || ips.length <= 0) {
            return;
        }
        try {
            for (String ip : ips) {
                String[] segs = StringUtils.split(ip, ".", true, true);
                if (segs == null || segs.length <= 0) {
                    continue;
                }
                List<Byte> list = new ArrayList<>();
                for (String seg : segs) {
                    list.add((byte)Integer.parseInt(seg));
                }
                set.add(list);
            }
        } catch (NumberFormatException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Parse filter fail, filter: %s.", ex);
            }
        }
    }

    /**
     * 设置过滤的黑白名单，以白名单为优先
     *
     * @param allows 半角逗号分割的白名单IP
     * @param blocks 半角逗号分割的黑名单IP
     */
    public void setSecurity(String allows, String blocks) {
        if (!StringUtils.isBlank(allows)) {
            // 设置白名单
            this.addFilter(allows, this.allows);
        }
        if (this.allows.isEmpty() && !StringUtils.isBlank(blocks)) {
            // 设置黑名单
            this.addFilter(blocks, this.blocks);
        }
        if (this.allows.isEmpty() && this.blocks.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There may not be set the security role for Websocket server.");
            }
        }
    }

    private boolean found(Set<List<Byte>> filter, byte[] segs) {
        for (List<Byte> list : filter) {
            boolean found = true;
            for (int index = 0; index < Math.min(list.size(), segs.length); index ++) {
                if (list.get(index) != segs[index]) {
                    found = false;
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }

    private boolean allow(Session session) {
        if (allows.isEmpty() && blocks.isEmpty()) {
            return true;
        } else {
            byte[] segs = session.getRemoteAddress().getAddress().getAddress();
            if (!allows.isEmpty()) {
                return found(allows, segs);
            }
            return !found(blocks, segs);
        }
    }

    /**
     * 客户端连接服务器时被回调，进行黑白名单判定
     *
     * @param session 会话
     */
    @OnWebSocketConnect
    public void onConnection(Session session) {
        if (allow(session)) {
            // TODO 处理在线
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Allow the connection: %s.",
                        TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress())));
            }
        } else {
            try {
                session.disconnect();
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The connection[%s] is blocked by the rules.",
                            TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress())));
                }
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Disconnect the remote[%s] fail.",
                            TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress())), ex);
                }
            }
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
    public void onClose(Session session, int statusCode, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Connection closed, status: %d, reason: %s.", statusCode, reason));
        }
    }

    /**
     * 发生错误时回调
     *
     * @param session   会话
     * @param throwable 错误异常
     */
    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error("Got error.", throwable);
        }
    }

    /**
     * 收到一个消息帧时回调
     *
     * @param session 会话
     * @param frame   消息帧
     */
    @OnWebSocketFrame
    public void onFrame(Session session, Frame frame) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a frame: %s.", frame.getClass().getName()));
        }
    }

    /**
     * 收到一个二进制消息时回调
     *
     * @param session 会话
     * @param in      输入流
     */
    @OnWebSocketMessage
    public void onBinaryMessage(Session session, InputStream in) {
        if (logger.isDebugEnabled()) {
            logger.debug("Got a binary message.");
        }
    }

    /**
     * 收到一个文本消息时回调
     *
     * @param session 会话
     * @param message 文本消息
     */
    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a text message: %s.", message));
        }
    }
}
