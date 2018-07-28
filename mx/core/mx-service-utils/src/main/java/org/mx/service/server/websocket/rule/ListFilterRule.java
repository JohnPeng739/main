package org.mx.service.server.websocket.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.service.server.WebsocketServerConfigBean;
import org.mx.service.server.websocket.WsSessionFilterRule;
import org.mx.service.server.websocket.WsSessionManager;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述： 根据黑白名单进行过滤的规则<br>
 * 黑白名单可能在加载的properties配置文件中进行如下定义：<br>
 * <pre>
 *     websocket.session.filter.rules.list.allows=127.0.0.1,::1,10.1
 *     websocket.session.filter.rules.list.blocks=168.192
 * </pre>
 * <strong>如果同时配置了黑白名单，以白名单为准。</strong>
 *
 * @author John.Peng
 * Date time 2018/3/10 下午7:02
 */
public class ListFilterRule implements WsSessionFilterRule {
    private static final Log logger = LogFactory.getLog(ListFilterRule.class);

    private WebsocketServerConfigBean.WebSocketFilter filter;

    private Set<byte[]> allows, blocks;

    /**
     * 默认的构造函数
     */
    public ListFilterRule() {
        super();
        this.allows = new HashSet<>();
        this.blocks = new HashSet<>();
    }

    /**
     * 构造函数
     *
     * @param filter 过滤器配置信息
     */
    public ListFilterRule(WebsocketServerConfigBean.WebSocketFilter filter) {
        this();
        this.filter = filter;
    }

    /**
     * 添加指定的过滤规则，按照IP字节顺序过滤
     *
     * @param filter 过滤规则
     * @param set    存放过滤集合
     */
    private void addFilter(String filter, Set<byte[]> set) {
        String[] ips = StringUtils.split(filter, ",", true, true);
        if (ips.length <= 0) {
            return;
        }
        try {
            for (String ip : ips) {
                byte[] values = TypeUtils.Ip2byteArray(ip);
                if (values != null && values.length > 0) {
                    set.add(values);
                }
            }
        } catch (NumberFormatException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Parse filter fail, filter: %s.", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionFilterRule#init(WsSessionManager)
     */
    @Override
    public void init(WsSessionManager manager) {
        String allowsStr = filter.getListAllows();
        if (!StringUtils.isBlank(allowsStr)) {
            // 设置白名单
            addFilter(allowsStr, allows);
        }
        String blocksStr = filter.getListBlocks();
        if (allows.isEmpty() && !StringUtils.isBlank(blocksStr)) {
            // 设置黑名单
            addFilter(blocksStr, blocks);
        }
        if (allows.isEmpty() && blocks.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("There may not be set the security role for Websocket server.");
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionFilterRule#destroy()
     */
    @Override
    public void destroy() {
        allows.clear();
        blocks.clear();
    }

    /**
     * 判定指定的seg是否在过滤规则中
     *
     * @param filter 过滤规则，白名单 or 黑名单
     * @param segs   IP字节数组
     * @return 如果存在，返回true，否则返回false。
     */
    private boolean found(Set<byte[]> filter, byte[] segs) {
        for (byte[] list : filter) {
            boolean found = true;
            for (int index = 0; index < Math.min(list.length, segs.length); index++) {
                if (list[index] != segs[index]) {
                    found = false;
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionFilterRule#filter(Session)
     */
    @Override
    public boolean filter(Session session) {
        if (allows.isEmpty() && blocks.isEmpty()) {
            return true;
        } else {
            byte[] segs = session.getRemoteAddress().getAddress().getAddress();
            if (!allows.isEmpty()) {
                return !found(allows, segs);
            }
            return found(blocks, segs);
        }
    }
}
