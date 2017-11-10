package com.ds.retl.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsProvider;
import org.mx.StringUtils;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ResourceAllocationException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于由容器提供的JMS功能的Provider，使用通用的JNDI方式连接MQ服务器。
 *
 * @author : john.peng created on date : 2014/12/19
 */
@SuppressWarnings("serial")
public class JndiJmsProvider implements JmsMultiProvider {
    private static final Log logger = LogFactory.getLog(JndiJmsProvider.class);
    private ConnectionFactory connectionFactory = null;
    private String defaultName = null;
    private Map<String, Destination> destinations = null;

    /**
     * 默认的构造函数
     */
    public JndiJmsProvider() {
        super();
        this.destinations = new HashMap<>();
    }

    /**
     * 默认的构造函数
     *
     * @param connectionFactoryName 连接MQ服务器的连接工厂名字，配置在JNDI中。
     * @param destinateName         连接MQ服务器上的队列（<code>Queue</code>）或者主题（<code>Topic</code> ）的名字，配置在JNDI中。
     * @throws JMSException 初始化JMS连接代理发生的异常。
     */
    public JndiJmsProvider(String connectionFactoryName, String destinateName)
            throws JMSException {
        this(connectionFactoryName, new String[]{destinateName});
    }

    /**
     * 默认的构造函数
     *
     * @param connectionFactoryName 连接MQ服务器的连接工厂名字，配置在JNDI中。
     * @param destinateNames        连接MQ服务器上的队列（<code>Queue</code>）或者主题（<code>Topic</code> ）的名字数组，配置在JNDI中。
     * @throws JMSException 初始化JMS连接代理发生的异常。
     */
    public JndiJmsProvider(String connectionFactoryName, String[] destinateNames)
            throws JMSException {
        this();
        if (StringUtils.isBlank(connectionFactoryName)) {
            String msg = "The ConnectionFactoryName is blank.";
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new ResourceAllocationException(msg);
        }
        if (destinateNames == null || destinateNames.length <= 0) {
            String msg = "The JMS destinate name array is null or is empty.";
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new ResourceAllocationException(msg);
        }
        try {
            InitialContext context = new InitialContext();
            // 获取QueueConnectionFactory对象
            connectionFactory = (ConnectionFactory) context
                    .lookup(connectionFactoryName);
            for (String destinateName : destinateNames) {
                if (StringUtils.isBlank(destinateName)) {
                    continue;
                }
                if (StringUtils.isBlank(defaultName)) {
                    defaultName = destinateName;
                }
                Destination destination;
                // 获取Destination对象
                destination = (Destination) context.lookup(destinateName);
                if (destination != null)
                    destinations.put(destinateName, destination);
            }
            if (logger.isErrorEnabled()) {
                logger.debug(String.format("Initialize JndiJmsProvider success，ConnectionFactoryName: %s, DestinateName: %s.",
                        connectionFactoryName, StringUtils.merge(destinateNames)));
            }
        } catch (NamingException ex) {
            String msg = String.format("Initialize JndiJmsProvider fail，ConnectionFactoryName: %s, DestinateName: %s.",
                    connectionFactoryName, StringUtils.merge(destinateNames));
            if (logger.isErrorEnabled()) {
                logger.error(msg, ex);
            }
            throw new ResourceAllocationException(msg);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see JmsProvider#connectionFactory()
     */
    @Override
    public ConnectionFactory connectionFactory() throws Exception {
        return connectionFactory;
    }

    /**
     * {@inheritDoc}
     *
     * @see JmsProvider#destination()
     */
    @Override
    public Destination destination() throws Exception {
        if (destinations != null && !destinations.isEmpty() && !StringUtils.isBlank(defaultName)) {
            return destinations.get(defaultName);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Destination> destinations() throws Exception {
        return destinations;
    }
}
