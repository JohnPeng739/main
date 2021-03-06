/**
 *
 */
package com.ds.retl.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsProvider;
import org.mx.StringUtils;

import javax.jms.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于直接连接ActiveMQ提供的JMS功能的Provider，使用了ActiveMQ提供的API。
 * ActiveMQ连接可以支持：tcp、udp、nio、ssl、http、vm等协议，默认端口为616161，ssl端口使用61617。
 *
 * @author : john.peng created on date : 2017/9/10
 */
public class ActiveMqJmsProvider implements JmsMultiProvider {
    private static final Log logger = LogFactory
            .getLog(ActiveMqJmsProvider.class);
    private ConnectionFactory connectionFactory = null;
    private String defaultName = null;
    private Map<String, Destination> destinations = null;

    public ActiveMqJmsProvider() {
        super();
        this.destinations = new HashMap<>();
    }

    /**
     * 构造函数
     *
     * @param connection    JMS连接字符串
     * @param user          用户名
     * @param password      密码
     * @param destinateName 连接目标点（队列或主题）名称
     * @throws JMSException 初始化过程中发生的异常
     */
    public ActiveMqJmsProvider(String connection, String user, String password,
                               String destinateName) throws JMSException {
        this(connection, user, password, new String[]{destinateName});
    }

    /**
     * 构造函数
     *
     * @param connection     JMS连接字符串
     * @param user           用户名
     * @param password       密码
     * @param destinateNames 连接目标点（队列或主题）名称数组
     * @throws JMSException 初始化过程中发生的异常
     */
    public ActiveMqJmsProvider(String connection, String user, String password,
                               String[] destinateNames) throws JMSException {
        this();
        boolean[] isTopics = new boolean[destinateNames.length];
        Arrays.fill(isTopics, false);
        this.init(connection, user, password, destinateNames, isTopics);
    }

    /**
     * 构造函数
     *
     * @param connection    JMS连接字符串
     * @param user          用户名
     * @param password      密码
     * @param destinateName 连接目标点（队列或主题）名称
     * @param isTopics      对应destinateName的位置的值如果设置为true，表示目标点为发布订阅主题；否则为点对点队列
     * @throws JMSException 初始化过程中发生的异常
     */
    public ActiveMqJmsProvider(String connection, String user, String password,
                               String destinateName, boolean[] isTopics) throws JMSException {
        this(connection, user, password, new String[]{destinateName}, isTopics);
    }

    /**
     * 构造函数
     *
     * @param connection     JMS连接字符串
     * @param user           用户名
     * @param password       密码
     * @param destinateNames 连接目标点（队列或主题）名称数组
     * @param isTopics       对应destinateName的位置的值如果设置为true，表示目标点为发布订阅主题；否则为点对点队列
     * @throws JMSException 初始化过程中发生的异常
     */
    public ActiveMqJmsProvider(String connection, String user, String password,
                               String[] destinateNames, boolean[] isTopics) throws JMSException {
        this();
        init(connection, user, password, destinateNames, isTopics);
    }

    private void init(String connection, String user, String password,
                      String[] destinateNames, boolean[] isTopics) throws JMSException {
        if (StringUtils.isBlank(connection)) {
            String msg = "The Connection String is blank.";
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
            // 根据输入的参数初始化ActiveMQ的连接
            connectionFactory = new ActiveMQConnectionFactory(user, password,
                    connection);
            Connection conn = connectionFactory.createConnection();
            // 这里统一使用CLIENT的确认方式。
            Session session = conn.createSession(false,
                    Session.CLIENT_ACKNOWLEDGE);
            Destination destination;
            for (int index = 0; index < destinateNames.length; index++) {
                String destinateName = destinateNames[index];
                boolean isTopic = isTopics[index];
                if (StringUtils.isBlank(destinateName)) {
                    continue;
                }
                if (StringUtils.isBlank(defaultName)) {
                    defaultName = destinateName;
                }
                if (isTopic) {
                    destination = session.createTopic(destinateName);
                } else {
                    destination = session.createQueue(destinateName);
                }
                if (destination != null) {
                    this.destinations.put(destinateName, destination);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Initialize ActiveMqJmsProvider success, connection: %s, user: %s, " +
                                    "password: %s, destinateName: %s, isTopic: %s",
                            connection, user, password, destinateName, isTopic ? "topic" : "queue"));
                }
            }
        } catch (JMSException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Initialize ActiveMqJmsProvider fail, connection: %s, user: %s, " +
                                "password: %s, destinateName: %s",
                        connection, user, password, StringUtils.merge(destinateNames)), ex);
            }
            throw ex;
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

    /**
     * {@inheritDoc}
     *
     * @see JmsMultiProvider#destinations()
     */
    @Override
    public Map<String, Destination> destinations() throws Exception {
        return destinations;
    }
}
