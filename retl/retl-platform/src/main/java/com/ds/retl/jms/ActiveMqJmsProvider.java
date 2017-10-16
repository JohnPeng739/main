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

/**
 * 用于直接连接ActiveMQ提供的JMS功能的Provider，使用了ActiveMQ提供的API。
 * ActiveMQ连接可以支持：tcp、udp、nio、ssl、http、vm等协议，默认端口为616161，ssl端口使用61617。
 *
 * @author : john.peng created on date : 2017/9/10
 */
public class ActiveMqJmsProvider implements JmsProvider {
    private static final Log logger = LogFactory
            .getLog(ActiveMqJmsProvider.class);
    private ConnectionFactory connectionFactory = null;
    private Destination destination = null;


    /**
     * 构造函数
     *
     * @param connection    JMS连接字符串
     * @param user          用户名
     * @param password      密码
     * @param destinateName 连接目标点（队列或主题）
     * @throws JMSException 初始化过程中发生的异常
     */
    public ActiveMqJmsProvider(String connection, String user, String password,
                               String destinateName) throws JMSException {
        this(connection, user, password, destinateName, false);
    }

    /**
     * 构造函数
     *
     * @param connection    JMS连接字符串
     * @param user          用户名
     * @param password      密码
     * @param destinateName 连接目标点（队列或主题）
     * @param isTopic       如果设置为true，表示目标点为发布订阅主题；否则为点对点队列
     * @throws JMSException 初始化过程中发生的异常
     */
    public ActiveMqJmsProvider(String connection, String user, String password,
                               String destinateName, Boolean isTopic) throws JMSException {
        if (StringUtils.isBlank(connection)) {
            String msg = "The Connection String is blank.";
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new ResourceAllocationException(msg);
        }
        if (StringUtils.isBlank(destinateName)) {
            String msg = "The DestinateName is blank.";
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
            if (isTopic) {
                destination = session.createTopic(destinateName);
            } else {
                destination = session.createQueue(destinateName);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Initialize ActiveMqJmsProvider success, connection: %s, user: %s, " +
                                "password: %s, destinateName: %s, isTopic: %s",
                        connection, user, password, destinateName, isTopic ? "topic" : "queue"));
            }
        } catch (JMSException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Initialize ActiveMqJmsProvider fail, connection: %s, user: %s, " +
                                "password: %s, destinateName: %s, isTopic: %s",
                        connection, user, password, destinateName, isTopic ? "topic" : "queue"));
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
        return destination;
    }

}
