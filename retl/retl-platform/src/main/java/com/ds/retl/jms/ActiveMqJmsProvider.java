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
 */
public class ActiveMqJmsProvider implements JmsProvider {
    private static final Log logger = LogFactory
            .getLog(ActiveMqJmsProvider.class);
    private ConnectionFactory connectionFactory = null;
    private Destination destination = null;

    /**
     * 默认的构造函数
     */
    public ActiveMqJmsProvider(String connection, String user, String password,
                               String destinateName) throws JMSException {
        this(connection, user, password, destinateName, false);
    }

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

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.contrib.jms.JmsProvider#connectionFactory()
     */
    @Override
    public ConnectionFactory connectionFactory() throws Exception {
        return connectionFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see backtype.storm.contrib.jms.JmsProvider#destination()
     */
    @Override
    public Destination destination() throws Exception {
        return destination;
    }

}
