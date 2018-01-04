package org.mx.service.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.service.ws.rule.ConnectFilterRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 连接规则工厂
 *
 * @author : john.peng created on date : 2018/1/4
 */
@Component("connectRuleFactory")
public class ConnectRuleFactory {
    private static final Log logger = LogFactory.getLog(ConnectRuleFactory.class);

    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    private Set<ConnectFilterRule> rules = null;

    /**
     * 默认的构造函数
     */
    public ConnectRuleFactory() {
        super();
        this.rules = new HashSet<>();
    }

    /**
     * 获取当前配置的Websocket连接过滤规则列表
     *
     * @return 规则列表
     */
    public Set<ConnectFilterRule> getRules() {
        return rules;
    }

    /**
     * 工厂初始化方法
     */
    public void init() {
        int num = env.getProperty("rules.num", Integer.class, 0);
        if (num <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("There does not define any connect filter rules, may be unsafe.");
            }
            return;
        }
        for (int index = 1; index <= num; index++) {
            String key = env.getProperty(String.format("rules.%d.key", index), "");
            String clazz = env.getProperty(String.format("rules.%d.class", index), "");
            if (StringUtils.isBlank(key) || StringUtils.isBlank(clazz)) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The connect filter rule's configuration error, key: %s, class: %s.",
                            key, clazz));
                }
                return;
            }
            try {
                ConnectFilterRule rule = (ConnectFilterRule) Class.forName(clazz).newInstance();
                rule.init(context, key);
                rules.add(rule);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Init connect filter rule successfully, key: %s, class: %s",
                            key, clazz));
                }
            } catch (ClassNotFoundException | InstantiationException | ClassCastException | IllegalAccessException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Init connect filter rule fail, class: %s.", clazz), ex);
                }
                return;
            }
        }
    }
}
