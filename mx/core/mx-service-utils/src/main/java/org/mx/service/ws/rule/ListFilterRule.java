package org.mx.service.ws.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于白名单、黑名单的连接过滤规则实现
 *
 * @author : john.peng created on date : 2018/1/4
 */
public class ListFilterRule implements ConnectFilterRule {
    private static final Log logger = LogFactory.getLog(ListFilterRule.class);

    private Set<List<Byte>> allows, blocks;

    public ListFilterRule() {
        super();
        this.allows = new HashSet<>();
        this.blocks = new HashSet<>();
    }

    /**
     * 添加指定的过滤规则，按照IP字节顺序过滤
     *
     * @param filter 过滤规则
     * @param set    存放过滤集合
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
                    list.add((byte) Integer.parseInt(seg));
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
     * 判定指定的seg是否在过滤规则中
     *
     * @param filter 过滤规则，白名单 or 黑名单
     * @param segs   IP字节数组
     * @return 如果存在，返回true，否则返回false。
     */
    private boolean found(Set<List<Byte>> filter, byte[] segs) {
        for (List<Byte> list : filter) {
            boolean found = true;
            for (int index = 0; index < Math.min(list.size(), segs.length); index++) {
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

    /**
     * {@inheritDoc}
     *
     * @see ConnectFilterRule#filter(Session)
     */
    @Override
    public boolean filter(Session session) {
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
     * {@inheritDoc}
     *
     * @see ConnectFilterRule#init(ApplicationContext, String)
     */
    @Override
    public void init(ApplicationContext context, String key) {
        if (context == null || StringUtils.isBlank(key)) {
            if (logger.isErrorEnabled()) {
                logger.error("The list filter rule' configuration error");
            }
            return;
        }
        Environment env = context.getEnvironment();
        String allows = env.getProperty(String.format("%s.allows", key));
        if (!StringUtils.isBlank(allows)) {
            // 设置白名单
            this.addFilter(allows, this.allows);
        }
        String blocks = env.getProperty(String.format("%s.blocks", key));
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

    /**
     * {@inheritDoc}
     *
     * @see ConnectFilterRule#getName()
     */
    @Override
    public String getName() {
        return "List filter rule";
    }
}
